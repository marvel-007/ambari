/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.checks;

import java.util.HashMap;
import java.util.Map;

import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.controller.PrereqCheckRequest;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.DesiredConfig;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.stack.PrereqCheckStatus;
import org.apache.ambari.server.state.stack.PrerequisiteCheck;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.inject.Provider;


/* Test for RangerSSLConfigCheck */
public class RangerSSLConfigCheckTest {

  private final Clusters clusters = Mockito.mock(Clusters.class);
  private final RangerSSLConfigCheck rangerSSLConfigCheck = new RangerSSLConfigCheck();

  @Before
  public void setup() {
    rangerSSLConfigCheck.clustersProvider = new Provider<Clusters>() {
      @Override
      public Clusters get() {
        return clusters;
      }
    };
    Configuration config = Mockito.mock(Configuration.class);
    rangerSSLConfigCheck.config = config;
  }

  @Test
  public void testIsApplicable() throws Exception {
    final Cluster cluster = Mockito.mock(Cluster.class);
    final Map<String, Service> services = new HashMap<>();
    final Service service = Mockito.mock(Service.class);

    services.put("RANGER", service);

    Mockito.when(cluster.getServices()).thenReturn(services);
    Mockito.when(cluster.getClusterId()).thenReturn(1L);
    Mockito.when(clusters.getCluster("cluster")).thenReturn(cluster);

    Assert.assertTrue(rangerSSLConfigCheck.isApplicable(new PrereqCheckRequest("cluster")));

    services.remove("RANGER");
    Assert.assertFalse(rangerSSLConfigCheck.isApplicable(new PrereqCheckRequest("cluster")));

  }

  @Test
  public void testPerform() throws Exception {
    final Cluster cluster = Mockito.mock(Cluster.class);
    final Map<String, Service> services = new HashMap<>();
    final Service service = Mockito.mock(Service.class);

    services.put("RANGER", service);

    Mockito.when(cluster.getServices()).thenReturn(services);
    Mockito.when(cluster.getClusterId()).thenReturn(1L);
    Mockito.when(clusters.getCluster("cluster")).thenReturn(cluster);

    final DesiredConfig desiredConfig = Mockito.mock(DesiredConfig.class);
    Mockito.when(desiredConfig.getTag()).thenReturn("tag");
    Map<String, DesiredConfig> configMap = new HashMap<String, DesiredConfig>();
    configMap.put("ranger-admin-site", desiredConfig);

    Mockito.when(cluster.getDesiredConfigs()).thenReturn(configMap);
    final Config config = Mockito.mock(Config.class);
    Mockito.when(cluster.getConfig(Mockito.anyString(), Mockito.anyString())).thenReturn(config);
    final Map<String, String> properties = new HashMap<String, String>();
    Mockito.when(config.getProperties()).thenReturn(properties);

    properties.put("ranger.service.http.enabled","true");
    properties.put("ranger.service.https.attrib.ssl.enabled","true");
    properties.put("ranger.https.attrib.keystore.file","/etc/ranger/security/ranger-admin-keystore.jks");
    PrerequisiteCheck check = new PrerequisiteCheck(null, null);
    rangerSSLConfigCheck.perform(check, new PrereqCheckRequest("cluster"));
    Assert.assertEquals(PrereqCheckStatus.PASS, check.getStatus());

    properties.put("ranger.service.http.enabled","true");
    properties.put("ranger.service.https.attrib.ssl.enabled","true");
    properties.put("ranger.https.attrib.keystore.file","/etc/ranger/security/ranger-admin-keystore.jks");
    check = new PrerequisiteCheck(null, null);
    rangerSSLConfigCheck.perform(check, new PrereqCheckRequest("cluster"));
    Assert.assertEquals(PrereqCheckStatus.PASS, check.getStatus());


    properties.put("ranger.service.http.enabled","true");
    properties.put("ranger.service.https.attrib.ssl.enabled","fasle");
    properties.put("ranger.https.attrib.keystore.file","/etc/ranger/security/ranger-admin-keystore.jks");
    check = new PrerequisiteCheck(null, null);
    rangerSSLConfigCheck.perform(check, new PrereqCheckRequest("cluster"));
    Assert.assertEquals(PrereqCheckStatus.PASS, check.getStatus());


    properties.put("ranger.service.http.enabled","false");
    properties.put("ranger.service.https.attrib.ssl.enabled","true");
    properties.put("ranger.https.attrib.keystore.file","/etc/ranger/admin/conf/ranger-admin-keystore.jks");
    check = new PrerequisiteCheck(null, null);
    rangerSSLConfigCheck.perform(check, new PrereqCheckRequest("cluster"));
    Assert.assertEquals(PrereqCheckStatus.WARNING, check.getStatus());

    properties.put("ranger.service.http.enabled","false");
    properties.put("ranger.service.https.attrib.ssl.enabled","true");
    properties.put("ranger.https.attrib.keystore.file","/etc/ranger/security/ranger-admin-keystore.jks");
    check = new PrerequisiteCheck(null, null);
    rangerSSLConfigCheck.perform(check, new PrereqCheckRequest("cluster"));
    Assert.assertEquals(PrereqCheckStatus.PASS, check.getStatus());


    properties.put("ranger.service.http.enabled","false");
    properties.put("ranger.service.https.attrib.ssl.enabled","false");
    properties.put("ranger.https.attrib.keystore.file","/etc/ranger/security/ranger-admin-keystore.jks");
    check = new PrerequisiteCheck(null, null);
    rangerSSLConfigCheck.perform(check, new PrereqCheckRequest("cluster"));
    Assert.assertEquals(PrereqCheckStatus.PASS, check.getStatus());



  }
}


