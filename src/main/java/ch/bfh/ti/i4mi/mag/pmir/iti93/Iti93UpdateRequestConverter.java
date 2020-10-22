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

import javax.xml.bind.JAXBException;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.MessageHeader;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.HTTPVerb;
import org.hl7.fhir.r4.model.Organization.OrganizationContactComponent;
import org.hl7.fhir.r4.model.Patient.PatientCommunicationComponent;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Timestamp;

import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import net.ihe.gazelle.hl7v3.coctmt090003UV01.COCTMT090003UV01AssignedEntity;
import net.ihe.gazelle.hl7v3.coctmt090003UV01.COCTMT090003UV01Organization;
import net.ihe.gazelle.hl7v3.coctmt150002UV01.COCTMT150002UV01Organization;
import net.ihe.gazelle.hl7v3.coctmt150003UV03.COCTMT150003UV03ContactParty;
import net.ihe.gazelle.hl7v3.coctmt150003UV03.COCTMT150003UV03Organization;
import net.ihe.gazelle.hl7v3.coctmt150003UV03.COCTMT150003UV03Person;
import net.ihe.gazelle.hl7v3.datatypes.AD;
import net.ihe.gazelle.hl7v3.datatypes.BL;
import net.ihe.gazelle.hl7v3.datatypes.CD;
import net.ihe.gazelle.hl7v3.datatypes.CE;
import net.ihe.gazelle.hl7v3.datatypes.CS;
import net.ihe.gazelle.hl7v3.datatypes.II;
import net.ihe.gazelle.hl7v3.datatypes.INT;
import net.ihe.gazelle.hl7v3.datatypes.ON;
import net.ihe.gazelle.hl7v3.datatypes.TS;
import net.ihe.gazelle.hl7v3.mccimt000100UV01.MCCIMT000100UV01Device;
import net.ihe.gazelle.hl7v3.mccimt000100UV01.MCCIMT000100UV01Receiver;
import net.ihe.gazelle.hl7v3.mccimt000100UV01.MCCIMT000100UV01Sender;
import net.ihe.gazelle.hl7v3.mfmimt700701UV01.MFMIMT700701UV01Custodian;
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
import net.ihe.gazelle.hl7v3.prpamt201301UV02.PRPAMT201301UV02LanguageCommunication;
import net.ihe.gazelle.hl7v3.prpamt201301UV02.PRPAMT201301UV02Patient;
import net.ihe.gazelle.hl7v3.prpamt201301UV02.PRPAMT201301UV02Person;
import net.ihe.gazelle.hl7v3.prpamt201302UV02.PRPAMT201302UV02LanguageCommunication;
import net.ihe.gazelle.hl7v3.prpamt201302UV02.PRPAMT201302UV02OtherIDs;
import net.ihe.gazelle.hl7v3.prpamt201302UV02.PRPAMT201302UV02OtherIDsId;
import net.ihe.gazelle.hl7v3.prpamt201302UV02.PRPAMT201302UV02Patient;
import net.ihe.gazelle.hl7v3.prpamt201302UV02.PRPAMT201302UV02PatientId;
import net.ihe.gazelle.hl7v3.prpamt201302UV02.PRPAMT201302UV02PatientPatientPerson;
import net.ihe.gazelle.hl7v3.prpamt201302UV02.PRPAMT201302UV02PatientStatusCode;
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

public class Iti93UpdateRequestConverter extends Iti93AddRequestConverter { 

	public String doUpdate(MessageHeader header, Map<String, BundleEntryComponent> entriesByReference) throws JAXBException {
		PRPAIN201302UV02Type resultMsg = new PRPAIN201302UV02Type();		
		  resultMsg.setITSVersion("XML_1.0");
		  //String UUID.randomUUID().toString();
		  resultMsg.setId(new II(config.getPixQueryOid(), uniqueId()));
		  resultMsg.setCreationTime(new TS(Timestamp.now().toHL7())); // Now
		  resultMsg.setProcessingCode(new CS("T", null ,null));
		  resultMsg.setProcessingModeCode(new CS("T", null, null));
		  resultMsg.setInteractionId(new II("2.16.840.1.113883.1.18", "PRPA_IN201302UV02"));
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
		 
		  PRPAIN201302UV02MFMIMT700701UV01ControlActProcess controlActProcess = new PRPAIN201302UV02MFMIMT700701UV01ControlActProcess();		  
		  resultMsg.setControlActProcess(controlActProcess);
		  controlActProcess.setClassCode(ActClassControlAct.CACT); 
		  controlActProcess.setMoodCode(XActMoodIntentEvent.EVN); 
		  controlActProcess.setCode(new CD("PRPA_TE201302UV02","2.16.840.1.113883.1.18", null)); 
		
		  for (BundleEntryComponent entry : entriesByReference.values()) {
			  //BundleEntryComponent entry = entriesByReference.get(ref.getReference());
		  	    	
	    	if (entry.getResource() instanceof Patient) {
	    		HTTPVerb method = entry.getRequest().getMethod();
		    	if (method == null) throw new InvalidRequestException("HTTP verb missing in Bundle for Patient resource.");
		    			    			    	
		    	Patient in = (Patient) entry.getResource();
		    			    			    	
		    	PRPAIN201302UV02MFMIMT700701UV01Subject1 subject = new PRPAIN201302UV02MFMIMT700701UV01Subject1();		    	
			  controlActProcess.addSubject(subject);
			  subject.setTypeCode("SUBJ");
			  subject.setContextConductionInd(false); // ???
			  
			  PRPAIN201302UV02MFMIMT700701UV01RegistrationEvent registrationEvent = new PRPAIN201302UV02MFMIMT700701UV01RegistrationEvent();			  
			  subject.setRegistrationEvent(registrationEvent);
			  registrationEvent.setClassCode(ActClass.REG);
			  registrationEvent.setMoodCode(ActMood.EVN);
			  registrationEvent.setStatusCode(new CS("active",null,null)); // ???
			  
			  PRPAIN201302UV02MFMIMT700701UV01Subject2 subject1 = new PRPAIN201302UV02MFMIMT700701UV01Subject2();
			  			  
			  registrationEvent.setSubject1(subject1);
			  subject1.setTypeCode(ParticipationTargetSubject.SBJ);
			  
			  PRPAMT201302UV02Patient patient = new PRPAMT201302UV02Patient();			  
			  subject1.setPatient(patient);
			  patient.setClassCode("PAT");
			  			  
			  PRPAMT201302UV02PatientStatusCode statusCode = new PRPAMT201302UV02PatientStatusCode();
			  statusCode.setCode("active");
			  patient.setStatusCode(statusCode ); //???
			  
			  PRPAMT201302UV02PatientPatientPerson patientPerson = new PRPAMT201302UV02PatientPatientPerson();
			  patient.setPatientPerson(patientPerson);
			  patientPerson.setClassCode(EntityClass.PSN);
			  patientPerson.setDeterminerCode(EntityDeterminer.INSTANCE);
			  patientPerson.setAsOtherIDs(new ArrayList());
			  // TODO How is the correct mapping done?
			    for (Identifier id : in.getIdentifier()) {	
			    	boolean isOwn = "urn:oid:1.3.6.1.4.1.21367.2017.2.5.83".equals(id.getSystem());
					if (isOwn) patient.addId(patientIdentifierUpd(id) );
					else {
					PRPAMT201302UV02OtherIDs asOtherIDs = new PRPAMT201302UV02OtherIDs();
					PRPAMT201302UV02OtherIDsId id2 = new PRPAMT201302UV02OtherIDsId();
					id2.setRoot(getScheme(id.getSystem()));
					id2.setExtension(id.getValue());
					   
					
					asOtherIDs.setClassCode("PAT");
					asOtherIDs.setId(Collections.singletonList(id2) );
					COCTMT150002UV01Organization scopingOrganization = new COCTMT150002UV01Organization();
					scopingOrganization.setClassCode(EntityClassOrganization.ORG);
					scopingOrganization.setDeterminerCode(EntityDeterminer.INSTANCE);
					List<II> scopeOrgIds = new ArrayList<II>();
					scopeOrgIds.add(new II(getScheme(id.getSystem()), null));
					scopingOrganization.setId(scopeOrgIds);
					asOtherIDs.setScopingOrganization(scopingOrganization );
					patientPerson.addAsOtherIDs(asOtherIDs );
					}				
			    }
			    
			   
		    	for (HumanName name : in.getName()) {		    				    	
					patientPerson.addName(transform(name));	
		    	}
		    	
		    	
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
		        
		        List<II> orgIds = new ArrayList<II>();
		        Organization managingOrg = getManagingOrganization(in);
		        for (Identifier id : managingOrg.getIdentifier()) {
		        	orgIds.add(new II(getScheme(id.getSystem()), null));
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
		        		PRPAMT201302UV02LanguageCommunication languageCommunication = new PRPAMT201302UV02LanguageCommunication();
		        		languageCommunication.setLanguageCode(transform(pcc.getLanguage()));
		        		if (pcc.hasPreferred()) languageCommunication.setPreferenceInd(new BL(pcc.getPreferred()));
						patientPerson.addLanguageCommunication(languageCommunication);
		        	}
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
				
				MFMIMT700701UV01Custodian custodian = new MFMIMT700701UV01Custodian();
				registrationEvent.setCustodian(custodian );
				custodian.setTypeCode(ParticipationType.CST);
				
				COCTMT090003UV01AssignedEntity assignedEntity = new COCTMT090003UV01AssignedEntity();
				custodian.setAssignedEntity(assignedEntity);
				assignedEntity.setClassCode(RoleClassAssignedEntity.ASSIGNED);
				
				List<II> custIds = new ArrayList<II>();			        			       
			    custIds.add(new II(getScheme("1.3.6.1.4.1.21367.2017.2.5.83"), null));
				
				assignedEntity.setId(custIds);
				//assignedEntity.setId(orgIds);								
				
				COCTMT090003UV01Organization assignedOrganization = new COCTMT090003UV01Organization();
				assignedEntity.setAssignedOrganization(assignedOrganization );
				assignedOrganization.setClassCode(EntityClassOrganization.ORG);
				assignedOrganization.setDeterminerCode(EntityDeterminer.INSTANCE);
				if (managingOrg.hasName()) {	
				  assignedOrganization.setName(Collections.singletonList(name));
				}
	    	}
	    	
	    	
	    }
	    
	    ByteArrayOutputStream out = new ByteArrayOutputStream();	    
	    HL7V3Transformer.marshallMessage(PRPAIN201302UV02Type.class, out, resultMsg);
	    System.out.println("POST CONVERT");
	    String outArray = new String(out.toByteArray()); 
	    System.out.println(outArray);
	    return outArray;
	}
}
