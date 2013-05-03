/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.reddwarf.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.reddwarf.v1.RedDwarfApi;
import org.jclouds.openstack.reddwarf.v1.domain.Flavor;
import org.jclouds.openstack.reddwarf.v1.internal.BaseRedDwarfApiExpectTest;
import org.testng.annotations.Test;

/**
 * Tests FlavorApi Guice wiring and parsing
 *
 * @author Zack Shoylev
 */
@Test(groups = "unit", testName = "FlavorApiExpectTest")
public class FlavorApiExpectTest extends BaseRedDwarfApiExpectTest {

   public void testListFlavors() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/flavors");
      FlavorApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/flavor_list.json")).build()
      ).getFlavorApiForZone("RegionOne");

      Set<? extends Flavor> flavors = api.list().toSet();
      assertEquals(flavors.size(),6);
      assertEquals(flavors.iterator().next().getRam(), 512);
   }

   public void testListFlavorsFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/flavors");
      FlavorApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getFlavorApiForZone("RegionOne");

      Set<? extends Flavor> flavors = api.list().toSet();
      assertTrue(flavors.isEmpty());
   }   

   public void testGetFlavor() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/flavors/1");
      FlavorApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/flavor_get.json")).build()
      ).getFlavorApiForZone("RegionOne");

      Flavor flavor = api.get(1);
      assertEquals(flavor.getName(), "512MB Instance");
      assertEquals(flavor.getId(), 1);
      assertEquals(flavor.getRam(), 512);
      assertEquals(flavor.getLinks().size(), 2);
   }
   
   public void testGetFlavorByAccountId() {
	      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/flavors/40806637803162");
	      RedDwarfApi redDwarfApi = requestsSendResponses(
               keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess,
               authenticatedGET().endpoint(endpoint).build(),
               HttpResponse.builder().statusCode(200).payload(payloadFromResource("/flavor_list.json")).build() ); 
	      FlavorApi api = redDwarfApi.getFlavorApiForZone("RegionOne");

	      Set<? extends Flavor> flavors = api.list( redDwarfApi.getCurrentTenantId().get().getId() ).toSet();
	      Flavor flavor = flavors.iterator().next();
	      assertEquals(flavor.getName(), "512MB Instance");
	      assertEquals(flavor.getId(), 1);
	      assertEquals(flavor.getRam(), 512);
	      assertEquals(flavor.getLinks().size(), 2);
	   }

   public void testGetFlavorFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/3456/flavors/12312");
      FlavorApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getFlavorApiForZone("RegionOne");

      assertNull(api.get(12312));
   }   
}
