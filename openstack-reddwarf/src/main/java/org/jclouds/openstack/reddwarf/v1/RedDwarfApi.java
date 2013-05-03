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
package org.jclouds.openstack.reddwarf.v1;

import java.io.Closeable;
import java.util.Set;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.reddwarf.v1.features.FlavorApi;
import org.jclouds.openstack.reddwarf.v1.features.InstanceApi;
import org.jclouds.openstack.reddwarf.v1.features.UserApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Provides access to RedDwarf.
 *  
 * @see <a href="http://api.openstack.org/">API Doc</a>
 * @author Zack Shoylev
 */
public interface RedDwarfApi extends Closeable{
   /**
    * @return the Zone codes configured
    */
   @Provides
   @Zone
   Set<String> getConfiguredZones();
   
   /**
    * Provides access to Flavor features.
    */
   @Delegate
   FlavorApi getFlavorApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);
   
   /**
    * Provides access to Instance features.
    */
   @Delegate
   InstanceApi getInstanceApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);
   
   /**
    * Provides access to User features.
    */
   @Delegate
   @Path("/instances/{instanceId}")
   UserApi getUserApiForInstanceInZone(@PathParam("instanceId") String instanceId, 
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);
   
   /**
    * Provides the Tenant
    */
   @Provides 
   Optional<Tenant> getCurrentTenantId();
}
