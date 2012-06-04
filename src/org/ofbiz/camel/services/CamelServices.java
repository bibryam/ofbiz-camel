package org.ofbiz.camel.services;

import org.apache.camel.CamelExecutionException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.camel.loader.CamelContainer;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.util.Collections;
import java.util.Map;

public class CamelServices {
    private static String module = CamelServices.class.getName();

    public static Map<String, Object> sendCamelMessage(DispatchContext ctx, Map<String, Object> context) {
        Object body = context.get("body");
        String endpoint = (String) context.get("endpoint");
        Map<String, Object> headers = getHeaders(context);

        try {
            CamelContainer.getProducerTemplate().sendBodyAndHeaders(endpoint, body, headers);
        } catch (CamelExecutionException cee) {
            Debug.logError(cee, module);
            return ServiceUtil.returnError(cee.getMessage());
        }

        return ServiceUtil.returnSuccess();
    }

    private static Map<String, Object> getHeaders(Map<String, Object> context) {
        Map<String, Object> headers = (Map<String, Object>) context.get("headers");
        return headers != null ? headers : Collections.<String, Object>emptyMap();
    }
}

