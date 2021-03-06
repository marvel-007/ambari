/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.topology.validators;

import java.util.HashSet;
import java.util.Set;

import org.apache.ambari.server.topology.ClusterTopology;
import org.apache.ambari.server.topology.InvalidTopologyException;
import org.apache.ambari.server.topology.TopologyValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates configuration types related to services specified in the blueprint.
 * If the cluster creation template contains configuration types that are not related to services in the blueprint the
 * validator fails interrupting the cluster provisioning.
 */
public class ClusterConfigTypeValidator implements TopologyValidator {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClusterConfigTypeValidator.class);

  @Override
  public void validate(ClusterTopology topology) throws InvalidTopologyException {

    if (topology.getConfiguration() == null) {
      LOGGER.debug("No configuration is set into the topology");
      return;
    }

    if (topology.getConfiguration().getProperties() == null) {
      LOGGER.debug("No properties is set into the topology configuration");
      return;
    }

    // config types in from the request
    Set<String> clusterConfigTypes = topology.getConfiguration().getProperties().keySet();
    LOGGER.debug("Cluster config types: {}", clusterConfigTypes);

    if (clusterConfigTypes == null || clusterConfigTypes.isEmpty()) {
      LOGGER.debug("No config types to be checked.");
      return;
    }

    // collecting all config types for services in the blueprint (from the related stack)
    Set<String> serviceConfigTypes = new HashSet<>();
    for (String serviceName : topology.getBlueprint().getServices()) {
      serviceConfigTypes.addAll(topology.getBlueprint().getStack().getConfigurationTypes(serviceName));
    }

    // identifying invalid config types
    Set<String> configTypeIntersection = new HashSet<String>(serviceConfigTypes);

    // if the intersection is changed, there's been some wrong config type provided in the cluster configuration
    if (configTypeIntersection.retainAll(clusterConfigTypes)) {
      LOGGER.debug("Valid config types: {}", configTypeIntersection);

      // get the wrong  config types
      Set<String> invalidConfigTypes = new HashSet<>(clusterConfigTypes);
      invalidConfigTypes.removeAll(configTypeIntersection);

      LOGGER.error("The following config typess are wrong: {}", invalidConfigTypes);
      throw new InvalidTopologyException("The following configuration types are invalid: " + invalidConfigTypes.toString());
    }
  }
}
