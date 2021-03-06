/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rackspace.autoscale.v1.config;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.openstack.keystone.auth.domain.AuthInfo;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v3.domain.Token;
import org.jclouds.rackspace.autoscale.v1.AutoscaleApi;
import org.jclouds.rackspace.autoscale.v1.handlers.AutoscaleErrorHandler;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Provides;

/**
 * Configures the Autoscale connection.
 */
@ConfiguresHttpApi
public class AutoscaleHttpApiModule extends HttpApiModule<AutoscaleApi> {

   @Override
   protected void configure() {
      super.configure();
   }

   @Provides
   @Singleton
   public Multimap<URI, URI> aliases() {
      return ImmutableMultimap.<URI, URI>builder().build();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(AutoscaleErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(AutoscaleErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(AutoscaleErrorHandler.class);
   }

   @Provides
   Supplier<Optional<String>> supplyTenant(Supplier<AuthInfo> access) {
      return Suppliers.compose(GetTenant.INSTANCE, access);
   }

   private static enum GetTenant implements Function<AuthInfo, Optional<String>> {
      INSTANCE;
      public Optional<String> apply(AuthInfo in){
         if (in instanceof Access) {
            return Access.class.cast(in).getToken().getTenant().transform(new Function<Tenant, String>() {
               @Override
               public String apply(Tenant input) {
                  return input.getId();
               }
            });
         } else if (in instanceof Token) {
            // FIXME: What if user authenticated scoped to another project?
            return Optional.of(Token.class.cast(in).user().defaultProjectId());
         }
         return Optional.absent();
      }
   }
}
