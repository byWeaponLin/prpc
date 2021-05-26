package com.weaponlin.inf.prpc.spring.boot.starter.export;

import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.spring.boot.starter.AbstractRegister;
import com.weaponlin.inf.prpc.spring.boot.starter.annotation.ExportService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO 支持标注@ExportService注解的服务发布
 */
public class ServiceExportingRegister extends AbstractRegister implements ImportBeanDefinitionRegistrar {
    private static final String BEAN_NAME_SUFFIX = "ServiceExporterRegisterBean";

    @SuppressWarnings("rawtypes")
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Collection<BeanDefinition> candidates = getCandidates(resourceLoader);

        AnnotationBeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

        Map<Class, String> exportedClass = new HashMap<>();

        List<ServiceNameAndInterface> exportings = candidates.stream()
                .flatMap(candidate -> {
                    String serviceBeanName = beanNameGenerator.generateBeanName(candidate, registry);
                    Class<?> clazz = getClass(candidate.getBeanClassName());

                    List<Class<?>> serviceInterfaces = Arrays.stream(ClassUtils.getAllInterfacesForClass(clazz))
                            .filter(c -> !c.getCanonicalName().startsWith("java"))
                            .filter(c -> !c.getCanonicalName().startsWith("org.springframework"))
                            .filter(c -> c.getMethods().length > 0)
                            .collect(Collectors.toList());

                    return serviceInterfaces.stream()
                            .map(serviceInterface -> {
                                if (exportedClass.containsKey(serviceInterface)) {
                                    throw new PRPCException(
                                            "already exported interface " + serviceInterface + " with bean class name="
                                                    + exportedClass.get(serviceInterface));
                                }
                                exportedClass.put(serviceInterface, serviceBeanName);

                                ServiceNameAndInterface configure = new ServiceNameAndInterface();
                                configure.serviceInterface = serviceInterface;
                                configure.serviceName = serviceBeanName;
                                return configure;
                            });
                })
                .collect(Collectors.toList());

        exportings
                .forEach(configure -> {
                    BeanDefinition bd = BeanDefinitionBuilder.rootBeanDefinition(ServiceExporterRegisterBean.class)
                            .addPropertyReference("target", configure.serviceName)
                            .addPropertyValue("serviceInterface", configure.serviceInterface)
                            .getBeanDefinition();
                    String beanName = configure.serviceName + BEAN_NAME_SUFFIX;
                    registry.registerBeanDefinition(beanName, bd);
                });
    }

    protected Collection<BeanDefinition> getCandidates(ResourceLoader resourceLoader) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false, environment);

        scanner.addIncludeFilter(new AnnotationTypeFilter(ExportService.class));
        scanner.setResourceLoader(resourceLoader);
        return getBasePackages().stream()
                .flatMap(basePackage -> scanner.findCandidateComponents(basePackage).stream())
                .collect(Collectors.toSet());
    }

    static class ServiceNameAndInterface {
        private String serviceName;
        private Class<?> serviceInterface;
    }
}
