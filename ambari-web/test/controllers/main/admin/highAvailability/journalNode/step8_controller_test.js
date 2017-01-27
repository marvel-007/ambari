/**
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


var App = require('app');
require('controllers/main/admin/highAvailability/journalNode/step8_controller');

describe('App.ManageJournalNodeWizardStep8Controller', function () {
  var controller;

  beforeEach(function () {
    controller = App.ManageJournalNodeWizardStep8Controller.create();
  });

  describe('#stopHDFS', function() {

    beforeEach(function() {
      sinon.stub(controller, 'stopServices');
    });

    afterEach(function() {
      controller.stopServices.restore();
    });

    it('stopServices should be called', function() {
      controller.stopHDFS();
      expect(controller.stopServices.calledWith(['HDFS'], true)).to.be.true;
    });
  });

  describe('#startAllServices', function() {

    beforeEach(function() {
      sinon.stub(controller, 'startServices');
    });

    afterEach(function() {
      controller.startServices.restore();
    });

    it('stopServices should be called', function() {
      controller.startAllServices();
      expect(controller.startServices.calledWith(false)).to.be.true;
    });
  });
});