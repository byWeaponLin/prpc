package com.weaponlin.inf.prpc.spring.boot.starter.reference;

import com.weaponlin.inf.prpc.spring.boot.starter.AbstractRegister;
import com.weaponlin.inf.prpc.spring.boot.starter.annotation.ReferenceService;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReferenceServiceRegistrar extends AbstractRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanNameGenerator beanNameGenerator = new ReferenceServiceBeanNameGenerator();

        Collection<BeanDefinition> candidates = getCandidates(resourceLoader);

        Set<Class> references = candidates.stream()
                .flatMap(candidate -> {
                    Class<?> clazz = getClass(candidate.getBeanClassName());

                    return FieldUtils.getAllFieldsList(clazz)
                            .stream()
                            .filter(f -> f.getAnnotation(ReferenceService.class) != null)
                            .map(Field::getType);
                }).collect(Collectors.toSet());

        references.forEach(fieldType -> {
            BeanDefinition bd = BeanDefinitionBuilder.rootBeanDefinition(ReferenceServiceFactoryBean.class)
                    .addPropertyReference("prpcProperties", "prpcProperties")
                    .addPropertyReference("prpClient", "prpClient")
                    .addConstructorArgValue(fieldType)
                    .getBeanDefinition();
            String name = beanNameGenerator.generateBeanName(bd, registry);
            bd.setAttribute("factoryBeanObjectType", fieldType.getName());
            registry.registerBeanDefinition(name, bd);
        });
    }

    private Collection<BeanDefinition> getCandidates(ResourceLoader resourceLoader) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false, environment);
        scanner.addIncludeFilter(new AbstractTypeHierarchyTraversingFilter(true, false) {
            @Override
            protected boolean matchClassName(String className) {
                try {
                    Class<?> clazz = Class.forName(className);
                    List<Field> fields = FieldUtils.getAllFieldsList(clazz);
                    return fields
                            .stream()
                            .anyMatch( f -> f.getAnnotation(ReferenceService.class) != null);
                } catch (ClassNotFoundException e) {
                    throw new BeanInitializationException("class not found when match class name", e);
                }
            }
        });

        scanner.setResourceLoader(resourceLoader);
        return getBasePackages()
                .stream()
                .flatMap(basePackage -> scanner.findCandidateComponents(basePackage).stream())
                .collect(Collectors.toSet());
    }
}
