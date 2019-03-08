package com.thoughtmechanix.licenses.clients;

import com.thoughtmechanix.licenses.model.Organization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * The client for invoking the organization service by Spring DiscoveryClient and standard Spring RestTemplate
 * 
 * <p>There are the steps for invoking the organization service:
 * <ul>
 * 	<li>Step1: Use Spring DiscoveryClient to get all the instances of organization service registered with Eureka.
 *  <li>Step2: Get the URL of the first instance in the instance list.
 * </ul>Step3: Make a HTTP REST call to the instance of organization service by the standard Spring RestTemplment 
 * </>
 * 
 * @author  Wuyi Chen
 * @date    03/07/2019
 * @version 1.0
 * @since   1.0
 */
@Component
public class OrganizationDiscoveryClient {
	private static final Logger logger = LoggerFactory.getLogger(OrganizationDiscoveryClient.class);
	
    @Autowired
    private DiscoveryClient discoveryClient;

    public Organization getOrganization(String organizationId) {
        RestTemplate restTemplate = new RestTemplate();
        
        // Gets a list of all the instances of organization service registered with Eureka
        List<ServiceInstance> instances = discoveryClient.getInstances("organizationservice");

        if (instances.isEmpty()) {
        	return null;
        }
        
        // Get the URL of the first instance in the instance list.
        String serviceUri = String.format("%s/v1/organizations/%s",instances.get(0).getUri().toString(), organizationId);  // The IP address of the instance you call is NOT abstracted
        logger.debug("The Service URL: " + serviceUri);

        // Uses a standard Spring REST Template class to call the service
        ResponseEntity< Organization > restExchange = restTemplate.exchange(serviceUri, HttpMethod.GET, null, Organization.class, organizationId);
        
        return restExchange.getBody();
    }
}
