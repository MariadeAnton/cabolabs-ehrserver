package com.cabolabs.ehrserver.openehr.common.change_control

import com.cabolabs.ehrserver.openehr.common.generic.AuditDetails
import com.cabolabs.ehrserver.openehr.common.change_control.Contribution;
import com.cabolabs.ehrserver.openehr.common.change_control.Version
import com.cabolabs.ehrserver.openehr.common.generic.DoctorProxy
import com.cabolabs.ehrserver.openehr.common.generic.PatientProxy
import com.cabolabs.ehrserver.openehr.demographic.Person

import org.junit.*

import com.cabolabs.ehrserver.openehr.common.change_control.ContributionController;
import com.cabolabs.ehrserver.openehr.ehr.Ehr;

import grails.test.mixin.*

@TestFor(ContributionController)
@Mock([Contribution, Ehr, Version, AuditDetails, PatientProxy, DoctorProxy, Person])
class ContributionControllerTests {

   void setUp()
   {
      def pat = new Person(
            firstName: 'Pablo',
            lastName: 'Pazos',
            dob: new Date(81, 9, 24),
            sex: 'M',
            idCode: '4116238-0',
            idType: 'CI',
            role: 'pat',
            uid: '463456346345654')

      if (!pat.save()) println p.errors
     
     
      // Crea EHRs para los pacientes de prueba
      // Idem EhrController.createEhr
      def ehr = new Ehr(
         subject: new PatientProxy( value: pat.uid )
      )
      if (!ehr.save()) println ehr.errors
   }

   void tearDown()
   {
      // Tear down logic here
   }
   
    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        params["ehr"] = Ehr.get(1)
        params["audit"] = new AuditDetails(
            systemId: "CABOLABS EHR",
            timeCommitted: new Date(),
            changeType: "creation",
            committer: new DoctorProxy( name: "Dr. House")
        )
        params["uid"] = '35634634634634563'
        // no versions!
    }

    void testIndex() {
        controller.index()
        assert "/contribution/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.contributionInstanceList.size() == 0
        assert model.contributionInstanceTotal == 0
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/contribution/list'

        // FIXME: create a valid Contribution
        populateValidParams(params)
        def contribution = new Contribution(params)

        assert contribution.save() != null

        params.id = contribution.id

        def model = controller.show()

        assert model.contributionInstance == contribution
    }

}
