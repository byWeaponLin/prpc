package com.weaponlin.inf.prpc.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.loader.Extension;
import com.weaponlin.inf.prpc.protocol.prpc.PRequest;
import com.weaponlin.inf.prpc.protocol.prpc.PResponse;

@Extension(name = "fastjson")
public class FastjsonCodec implements PCodec {

    @Override
    public byte[] encode(Object o) {
        return JSON.toJSONString(o).getBytes(Charsets.UTF_8);
    }

    @Override
    public Object decode(byte[] bytes, Object o) {
        try {
            String json = new String(bytes, Charsets.UTF_8);
            JSONObject jsonObject = JSONObject.parseObject(json);
            if (o instanceof PRequest) {
                PRequest req = (PRequest) o;
                req.setGroup(jsonObject.getString("group"));
                req.setRequestId(jsonObject.getString("requestId"));
                req.setServiceName(jsonObject.getString("serviceName"));
                req.setMethodName(jsonObject.getString("methodName"));
                JSONArray typeArray = jsonObject.getJSONArray("parameterTypes");
                Class<?>[] parameterTypes = new Class[typeArray.size()];
                for (int i = 0; i < typeArray.size(); i++) {
                    parameterTypes[i] = Class.forName(typeArray.getString(i));
                }
                req.setParameterTypes(parameterTypes);
                //
                JSONArray paramArray = jsonObject.getJSONArray("params");
                Object[] params = new Object[paramArray.size()];
                for (int i = 0; i < paramArray.size(); i++) {
                    params[i] = paramArray.getObject(i, parameterTypes[i]);
                }
                req.setParams(params);
                return req;
            } else if (o instanceof PResponse) {
                PResponse res = (PResponse) o;
                res.setRequestId(jsonObject.getString("requestId"));
                res.setServiceName(jsonObject.getString("serviceName"));
                res.setMethodName(jsonObject.getString("methodName"));
                res.setResultType(jsonObject.getObject("resultType", Class.class));
                // TODO 需要判断result属性中的参数
                res.setResult(jsonObject.getObject("result", res.getResultType()));
                return res;
            }
            return JSONObject.parseObject(json).toJavaObject(o.getClass());
        } catch (Exception e) {
            throw new PRPCException("decode with fastjson failed", e);
        }
    }
}
