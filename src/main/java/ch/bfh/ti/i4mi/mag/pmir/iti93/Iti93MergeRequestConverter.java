/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.bfh.ti.i4mi.mag.pmir.iti93;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import javax.xml.bind.JAXBException;

import org.apache.camel.Body;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.MessageHeader;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Patient.LinkType;
import org.hl7.fhir.r4.model.Patient.PatientCommunicationComponent;
import org.hl7.fhir.r4.model.Patient.PatientLinkComponent;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Bundle.HTTPVerb;
import org.hl7.fhir.r4.model.Organization.OrganizationContactComponent;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Timestamp;

import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ch.bfh.ti.i4mi.mag.pmir.PMIRRequestConverter;
import net.ihe.gazelle.hl7v3.coctmt090003UV01.COCTMT090003UV01AssignedEntity;
import net.ihe.gazelle.hl7v3.coctmt090003UV01.COCTMT090003UV01Organization;
import net.ihe.gazelle.hl7v3.coctmt150003UV03.COCTMT150003UV03Organization;
import net.ihe.gazelle.hl7v3.coctmt150003UV03.COCTMT150003UV03ContactParty;
import net.ihe.gazelle.hl7v3.coctmt150003UV03.COCTMT150003UV03Person;
import net.ihe.gazelle.hl7v3.datatypes.AD;
import net.ihe.gazelle.hl7v3.datatypes.AdxpCity;
import net.ihe.gazelle.hl7v3.datatypes.AdxpCountry;
import net.ihe.gazelle.hl7v3.datatypes.AdxpPostalCode;
import net.ihe.gazelle.hl7v3.datatypes.AdxpState;
import net.ihe.gazelle.hl7v3.datatypes.AdxpStreetAddressLine;
import net.ihe.gazelle.hl7v3.datatypes.BL;
import net.ihe.gazelle.hl7v3.datatypes.CD;
import net.ihe.gazelle.hl7v3.datatypes.CE;
import net.ihe.gazelle.hl7v3.datatypes.CS;
import net.ihe.gazelle.hl7v3.datatypes.EnFamily;
import net.ihe.gazelle.hl7v3.datatypes.EnGiven;
import net.ihe.gazelle.hl7v3.datatypes.EnPrefix;
import net.ihe.gazelle.hl7v3.datatypes.EnSuffix;
import net.ihe.gazelle.hl7v3.datatypes.II;
import net.ihe.gazelle.hl7v3.datatypes.INT;
import net.ihe.gazelle.hl7v3.datatypes.ON;
import net.ihe.gazelle.hl7v3.datatypes.PN;
import net.ihe.gazelle.hl7v3.datatypes.TEL;
import net.ihe.gazelle.hl7v3.datatypes.TS;
import net.ihe.gazelle.hl7v3.mccimt000100UV01.MCCIMT000100UV01Device;
import net.ihe.gazelle.hl7v3.mccimt000100UV01.MCCIMT000100UV01Receiver;
import net.ihe.gazelle.hl7v3.mccimt000100UV01.MCCIMT000100UV01Sender;
import net.ihe.gazelle.hl7v3.mfmimt700701UV01.MFMIMT700701UV01Custodian;
import net.ihe.gazelle.hl7v3.mfmimt700701UV01.MFMIMT700701UV01PriorRegisteredRole;
import net.ihe.gazelle.hl7v3.mfmimt700701UV01.MFMIMT700701UV01PriorRegistration;
import net.ihe.gazelle.hl7v3.mfmimt700701UV01.MFMIMT700701UV01ReplacementOf;
import net.ihe.gazelle.hl7v3.mfmimt700701UV01.MFMIMT700701UV01Subject3;
import net.ihe.gazelle.hl7v3.prpain201301UV02.PRPAIN201301UV02MFMIMT700701UV01ControlActProcess;
import net.ihe.gazelle.hl7v3.prpain201301UV02.PRPAIN201301UV02MFMIMT700701UV01RegistrationEvent;
import net.ihe.gazelle.hl7v3.prpain201301UV02.PRPAIN201301UV02MFMIMT700701UV01Subject1;
import net.ihe.gazelle.hl7v3.prpain201301UV02.PRPAIN201301UV02MFMIMT700701UV01Subject2;
import net.ihe.gazelle.hl7v3.prpain201301UV02.PRPAIN201301UV02Type;
import net.ihe.gazelle.hl7v3.prpain201302UV02.PRPAIN201302UV02MFMIMT700701UV01ControlActProcess;
import net.ihe.gazelle.hl7v3.prpain201302UV02.PRPAIN201302UV02MFMIMT700701UV01RegistrationEvent;
import net.ihe.gazelle.hl7v3.prpain201302UV02.PRPAIN201302UV02MFMIMT700701UV01Subject1;
import net.ihe.gazelle.hl7v3.prpain201302UV02.PRPAIN201302UV02MFMIMT700701UV01Subject2;
import net.ihe.gazelle.hl7v3.prpain201302UV02.PRPAIN201302UV02Type;
import net.ihe.gazelle.hl7v3.prpain201304UV02.PRPAIN201304UV02MFMIMT700701UV01ControlActProcess;
import net.ihe.gazelle.hl7v3.prpain201304UV02.PRPAIN201304UV02MFMIMT700701UV01RegistrationEvent;
import net.ihe.gazelle.hl7v3.prpain201304UV02.PRPAIN201304UV02MFMIMT700701UV01Subject1;
import net.ihe.gazelle.hl7v3.prpain201304UV02.PRPAIN201304UV02MFMIMT700701UV01Subject2;
import net.ihe.gazelle.hl7v3.prpain201304UV02.PRPAIN201304UV02Type;
import net.ihe.gazelle.hl7v3.prpamt201301UV02.PRPAMT201301UV02LanguageCommunication;
import net.ihe.gazelle.hl7v3.prpamt201301UV02.PRPAMT201301UV02Patient;
import net.ihe.gazelle.hl7v3.prpamt201301UV02.PRPAMT201301UV02Person;
import net.ihe.gazelle.hl7v3.prpamt201302UV02.PRPAMT201302UV02Patient;
import net.ihe.gazelle.hl7v3.prpamt201302UV02.PRPAMT201302UV02PatientId;
import net.ihe.gazelle.hl7v3.prpamt201302UV02.PRPAMT201302UV02PatientPatientPerson;
import net.ihe.gazelle.hl7v3.prpamt201302UV02.PRPAMT201302UV02PatientStatusCode;
import net.ihe.gazelle.hl7v3.prpamt201302UV02.PRPAMT201302UV02Person;
import net.ihe.gazelle.hl7v3.prpamt201303UV02.PRPAMT201303UV02LanguageCommunication;
import net.ihe.gazelle.hl7v3.prpamt201303UV02.PRPAMT201303UV02Patient;
import net.ihe.gazelle.hl7v3.prpamt201303UV02.PRPAMT201303UV02Person;
import net.ihe.gazelle.hl7v3.voc.ActClass;
import net.ihe.gazelle.hl7v3.voc.ActClassControlAct;
import net.ihe.gazelle.hl7v3.voc.ActMood;
import net.ihe.gazelle.hl7v3.voc.CommunicationFunctionType;
import net.ihe.gazelle.hl7v3.voc.EntityClass;
import net.ihe.gazelle.hl7v3.voc.EntityClassDevice;
import net.ihe.gazelle.hl7v3.voc.EntityClassOrganization;
import net.ihe.gazelle.hl7v3.voc.EntityDeterminer;
import net.ihe.gazelle.hl7v3.voc.ParticipationTargetSubject;
import net.ihe.gazelle.hl7v3.voc.ParticipationType;
import net.ihe.gazelle.hl7v3.voc.RoleClassAssignedEntity;
import net.ihe.gazelle.hl7v3.voc.RoleClassContact;
import net.ihe.gazelle.hl7v3.voc.XActMoodIntentEvent;
import net.ihe.gazelle.hl7v3transformer.HL7V3Transformer;

/**
 * ITI-93 Patient Feed : Merge Request
 * @author alexander
 *
 */
public class Iti93MergeRequestConverter extends Iti93UpdateRequestConverter {


	/**
	 * merge request for ITI-93 transaction
	 * @param header
	 * @param entriesByReference
	 * @return
	 * @throws JAXBException
	 */
	public String doMerge(MessageHeader header, Map<String, BundleEntryComponent> entriesByReference) throws JAXBException {

		 PRPAIN201304UV02Type resultMsg = new PRPAIN201304UV02Type();
		  resultMsg.setITSVersion("XML_1.0");
		  //String UUID.randomUUID().toString();
		  resultMsg.setId(new II(config.getPixQueryOid(), uniqueId()));
		  resultMsg.setCreationTime(new TS(Timestamp.now().toHL7())); // Now
		  resultMsg.setProcessingCode(new CS("T", null ,null));
		  resultMsg.setProcessingModeCode(new CS("T", null, null));
		  resultMsg.setInteractionId(new II("2.16.840.1.113883.1.18", "PRPA_IN201304UV02"));
		  resultMsg.setAcceptAckCode(new CS("AL", null, null));

		  MCCIMT000100UV01Receiver receiver = new MCCIMT000100UV01Receiver();
		  resultMsg.addReceiver(receiver);
		  receiver.setTypeCode(CommunicationFunctionType.RCV);

		  MCCIMT000100UV01Device receiverDevice = new MCCIMT000100UV01Device();
		  receiver.setDevice(receiverDevice );
		  receiverDevice.setClassCode(EntityClassDevice.DEV);
		  receiverDevice.setDeterminerCode(EntityDeterminer.INSTANCE);
		  receiverDevice.setId(Collections.singletonList(new II(config.getPixReceiverOid(), null)));

		  MCCIMT000100UV01Sender sender = new MCCIMT000100UV01Sender();
		  resultMsg.setSender(sender);
		  sender.setTypeCode(CommunicationFunctionType.SND);

		  MCCIMT000100UV01Device senderDevice = new MCCIMT000100UV01Device();
		  sender.setDevice(senderDevice);
		  senderDevice.setClassCode(EntityClassDevice.DEV);
		  senderDevice.setDeterminerCode(EntityDeterminer.INSTANCE);
		  senderDevice.setId(Collections.singletonList(new II(config.getPixMySenderOid(), null)));

		  PRPAIN201304UV02MFMIMT700701UV01ControlActProcess controlActProcess = new PRPAIN201304UV02MFMIMT700701UV01ControlActProcess();
		  resultMsg.setControlActProcess(controlActProcess);
		  controlActProcess.setClassCode(ActClassControlAct.CACT); // ???
		  controlActProcess.setMoodCode(XActMoodIntentEvent.EVN); // ???
		  controlActProcess.setCode(new CD("PRPA_TE201304UV02","2.16.840.1.113883.1.18", null)); // ???

		  for (BundleEntryComponent entry : entriesByReference.values()) {
			  //BundleEntryComponent entry = entriesByReference.get(ref.getReference());

	    		HTTPVerb method = entry.getRequest().getMethod();
		    	if (method == null) throw new InvalidRequestException("HTTP verb missing in Bundle for Patient resource.");

		    	Patient basePatient = (Patient) entry.getResource();
		    	Patient in = null;

		    	List<Patient> replaced = new ArrayList<Patient>();

		    	for (PatientLinkComponent linked : basePatient.getLink()) {
		    		if (linked.getType().equals(LinkType.REPLACEDBY)) {
		    			Reference other = linked.getOther();
		    			Patient otherPatient = findPatient(other, entriesByReference, basePatient);
		    			in = otherPatient;
		    			replaced.add(basePatient);
		    		} else if (linked.getType().equals(LinkType.REPLACES)) {
		    			Reference other = linked.getOther();
		    			Patient otherPatient = findPatient(other, entriesByReference, basePatient);
		    			in = basePatient;
		    			replaced.add(otherPatient);
		    		}
		    	}

		    	if (in==null || replaced.isEmpty()) throw new InvalidRequestException("Cannot determine Patients to merge");

		      PRPAIN201304UV02MFMIMT700701UV01Subject1 subject = new PRPAIN201304UV02MFMIMT700701UV01Subject1();
			  controlActProcess.addSubject(subject);
			  subject.setTypeCode("SUBJ");
			  subject.setContextConductionInd(false); // ???

			  PRPAIN201304UV02MFMIMT700701UV01RegistrationEvent registrationEvent = new PRPAIN201304UV02MFMIMT700701UV01RegistrationEvent();
			  subject.setRegistrationEvent(registrationEvent);
			  registrationEvent.setClassCode(ActClass.REG);
			  registrationEvent.setMoodCode(ActMood.EVN);
			  registrationEvent.setStatusCode(new CS("active",null,null)); // ???

			  List<MFMIMT700701UV01ReplacementOf> replacementOfList = new ArrayList<MFMIMT700701UV01ReplacementOf>();
			  registrationEvent.setReplacementOf(replacementOfList);

			  for (Patient replacedPatient : replaced) {
			    MFMIMT700701UV01ReplacementOf replacementOf = new MFMIMT700701UV01ReplacementOf();
			    MFMIMT700701UV01PriorRegistration priorRegistration = new MFMIMT700701UV01PriorRegistration();
			    replacementOf.setPriorRegistration(priorRegistration );
			    replacementOf.setTypeCode("RPLC");
			    priorRegistration.setClassCode(ActClass.REG);
			    priorRegistration.setMoodCode(ActMood.EVN);
			    priorRegistration.setStatusCode(new CS("obsolete", null,null));
			    MFMIMT700701UV01Subject3 subject1 = new MFMIMT700701UV01Subject3();
				priorRegistration.setSubject1(subject1 );
			    subject1.setTypeCode(ParticipationTargetSubject.SBJ);

			    MFMIMT700701UV01PriorRegisteredRole priorRegisteredRole = new MFMIMT700701UV01PriorRegisteredRole();
				subject1.setPriorRegisteredRole(priorRegisteredRole );
				priorRegisteredRole.setClassCode("PAT");

				for (Identifier id : replacedPatient.getIdentifier()) {
					priorRegisteredRole.addId(patientIdentifier(id));
			    }

			    replacementOfList.add(replacementOf);
			  }


			  PRPAIN201304UV02MFMIMT700701UV01Subject2 subject1 = new PRPAIN201304UV02MFMIMT700701UV01Subject2();
			  registrationEvent.setSubject1(subject1);
			  subject1.setTypeCode(ParticipationTargetSubject.SBJ);

			  PRPAMT201303UV02Patient patient = new PRPAMT201303UV02Patient();
			  patient.setClassCode("PAT");
			  subject1.setPatient(patient);

			  PRPAMT201302UV02PatientStatusCode statusCode = new PRPAMT201302UV02PatientStatusCode("active", null, null);
			  patient.setStatusCode(statusCode); //???


			  List<II> orgIds = new ArrayList<II>();
			  Set<String> mainIds = new HashSet<String>();


			  PRPAMT201303UV02Person patientPerson = new PRPAMT201303UV02Person();
			  patientPerson.setClassCode(EntityClass.PSN);
			  patientPerson.setDeterminerCode(EntityDeterminer.INSTANCE);
			  patient.setPatientPerson(patientPerson );
			  Organization managingOrg = getManagingOrganization(in, basePatient.getContained());
			  for (Identifier id : managingOrg.getIdentifier()) {
					orgIds.add(new II(getScheme(id.getSystem()), null));
					mainIds.add(id.getSystem());
			  }
			  if (managingOrg==null) managingOrg = getManagingOrganization(basePatient, basePatient.getContained());
			  if (managingOrg==null)  throw new InvalidRequestException("Cannot determine managingOrganization");

			  // TODO How is the correct mapping done?
			    for (Identifier id : in.getIdentifier()) {
			    	PRPAMT201302UV02PatientId patientId = new PRPAMT201302UV02PatientId(getScheme(id.getSystem()),id.getValue());
					patient.addId(patientId );
			    }

		    	for (HumanName name : in.getName()) {
					patientPerson.addName(transform(name));
		    	}

				COCTMT150003UV03Organization providerOrganization = new COCTMT150003UV03Organization();
			   	patient.setProviderOrganization(providerOrganization);
			   	providerOrganization.setClassCode(EntityClassOrganization.ORG);
			   	providerOrganization.setDeterminerCode(EntityDeterminer.INSTANCE);

			   	providerOrganization.setId(orgIds);
			   	ON name = null;
			   	if (managingOrg.hasName()) {
				   name = new ON();
				   name.setMixed(Collections.singletonList(managingOrg.getName()));
				   providerOrganization.setName(Collections.singletonList(name));
			   	}

			   	COCTMT150003UV03ContactParty contactParty = new COCTMT150003UV03ContactParty();
			   	contactParty.setClassCode(RoleClassContact.CON);
			   	for (ContactPoint contactPoint : managingOrg.getTelecom()) {
					contactParty.addTelecom(transform(contactPoint));
			   	}

			   	if (managingOrg.hasAddress()) contactParty.setAddr(new ArrayList<AD>());
			   	for (Address address : managingOrg.getAddress()) {
					contactParty.addAddr(transform(address));
			   	}
			   	if (managingOrg.hasContact()) {
					OrganizationContactComponent occ = managingOrg.getContactFirstRep();
					COCTMT150003UV03Person contactPerson = new COCTMT150003UV03Person();
					contactPerson.setClassCode(EntityClass.PSN);
					contactPerson.setDeterminerCode(EntityDeterminer.INSTANCE);
					if (occ.hasName()) contactPerson.setName(Collections.singletonList(transform(occ.getName())));
					contactParty.setContactPerson(contactPerson);
			  	}
			   	providerOrganization.setContactParty(Collections.singletonList(contactParty));

		    	patientPerson.setBirthTime(transform(in.getBirthDateElement()));
		    	if (in.hasGender()) {
			        switch(in.getGender()) {
			        case MALE:patientPerson.setAdministrativeGenderCode(new CE("M","Male","2.16.840.1.113883.12.1"));break;
			        case FEMALE:patientPerson.setAdministrativeGenderCode(new CE("F","Female","2.16.840.1.113883.12.1"));break;
			        case OTHER:patientPerson.setAdministrativeGenderCode(new CE("A","Ambiguous","2.16.840.1.113883.12.1"));break;
			        case UNKNOWN:patientPerson.setAdministrativeGenderCode(new CE("U","Unknown","2.16.840.1.113883.12.1"));break;
			        }
		    	}
		        if (in.hasAddress()) patientPerson.setAddr(new ArrayList<AD>());
		        for (Address address : in.getAddress()) {
					patientPerson.addAddr(transform(address));
		        }

		        for (ContactPoint contactPoint : in.getTelecom()) {
					patientPerson.addTelecom(transform(contactPoint));
		        }

		        if (in.hasDeceasedBooleanType()) {
		          patientPerson.setDeceasedInd(new BL(in.getDeceasedBooleanType().getValue()));
		        }
		        if (in.hasDeceasedDateTimeType()) {
		        	patientPerson.setDeceasedTime(transform(in.getDeceasedDateTimeType()));
		        }
		        if (in.hasMultipleBirthBooleanType()) {
		        	patientPerson.setMultipleBirthInd(new BL(in.getMultipleBirthBooleanType().getValue()));
		        }
		        if (in.hasMultipleBirthIntegerType()) {
		        	patientPerson.setMultipleBirthOrderNumber(new INT(in.getMultipleBirthIntegerType().getValue()));
		        }
		        if (in.hasMaritalStatus()) {
		        	patientPerson.setMaritalStatusCode(transform(in.getMaritalStatus()));
		        }
		        if (in.hasCommunication()) {
		        	for (PatientCommunicationComponent pcc : in.getCommunication()) {
		        		PRPAMT201303UV02LanguageCommunication languageCommunication = new PRPAMT201303UV02LanguageCommunication();
		        		languageCommunication.setLanguageCode(transform(pcc.getLanguage()));
		        		if (pcc.hasPreferred()) languageCommunication.setPreferenceInd(new BL(pcc.getPreferred()));
						patientPerson.addLanguageCommunication(languageCommunication);
		        	}
		        }

		    	MFMIMT700701UV01Custodian custodian = new MFMIMT700701UV01Custodian();
				registrationEvent.setCustodian(custodian );
				custodian.setTypeCode(ParticipationType.CST);

				COCTMT090003UV01AssignedEntity assignedEntity = new COCTMT090003UV01AssignedEntity();
				custodian.setAssignedEntity(assignedEntity);
				assignedEntity.setClassCode(RoleClassAssignedEntity.ASSIGNED);

				List<II> custIds = new ArrayList<II>();
			    custIds.add(new II(getScheme(config.getCustodianOid()), null));

				assignedEntity.setId(custIds);
				//assignedEntity.setId(orgIds);

				COCTMT090003UV01Organization assignedOrganization = new COCTMT090003UV01Organization();
				assignedEntity.setAssignedOrganization(assignedOrganization );
				assignedOrganization.setClassCode(EntityClassOrganization.ORG);
				assignedOrganization.setDeterminerCode(EntityDeterminer.INSTANCE);
				if (managingOrg.hasName()) {
					ON onName = new ON();
					onName.setMixed(Collections.singletonList(managingOrg.getName()));
				  assignedOrganization.setName(Collections.singletonList(onName));
				}
	    	}


	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    HL7V3Transformer.marshallMessage(PRPAIN201304UV02Type.class, out, resultMsg);
	    System.out.println("POST CONVERT");
	    String outArray = new String(out.toByteArray());
	    System.out.println(outArray);
	    return outArray;
	}
}