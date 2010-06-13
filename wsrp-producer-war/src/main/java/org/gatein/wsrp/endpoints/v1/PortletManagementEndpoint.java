/*
 * JBoss, a division of Red Hat
 * Copyright 2010, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.gatein.wsrp.endpoints.v1;

import org.gatein.wsrp.WSRPUtils;
import org.gatein.wsrp.endpoints.WSRPBaseEndpoint;
import org.gatein.wsrp.spec.v1.V1ToV2Converter;
import org.gatein.wsrp.spec.v1.V2ToV1Converter;
import org.gatein.wsrp.spec.v1.WSRP1ExceptionFactory;
import org.oasis.wsrp.v1.V1AccessDenied;
import org.oasis.wsrp.v1.V1DestroyFailed;
import org.oasis.wsrp.v1.V1Extension;
import org.oasis.wsrp.v1.V1InconsistentParameters;
import org.oasis.wsrp.v1.V1InvalidHandle;
import org.oasis.wsrp.v1.V1InvalidRegistration;
import org.oasis.wsrp.v1.V1InvalidUserCategory;
import org.oasis.wsrp.v1.V1MissingParameters;
import org.oasis.wsrp.v1.V1ModelDescription;
import org.oasis.wsrp.v1.V1OperationFailed;
import org.oasis.wsrp.v1.V1PortletContext;
import org.oasis.wsrp.v1.V1PortletDescription;
import org.oasis.wsrp.v1.V1Property;
import org.oasis.wsrp.v1.V1PropertyList;
import org.oasis.wsrp.v1.V1RegistrationContext;
import org.oasis.wsrp.v1.V1ResetProperty;
import org.oasis.wsrp.v1.V1ResourceList;
import org.oasis.wsrp.v1.V1UserContext;
import org.oasis.wsrp.v1.WSRPV1PortletManagementPortType;
import org.oasis.wsrp.v2.AccessDenied;
import org.oasis.wsrp.v2.ClonePortlet;
import org.oasis.wsrp.v2.DestroyPortlets;
import org.oasis.wsrp.v2.DestroyPortletsResponse;
import org.oasis.wsrp.v2.GetPortletDescription;
import org.oasis.wsrp.v2.GetPortletProperties;
import org.oasis.wsrp.v2.GetPortletPropertyDescription;
import org.oasis.wsrp.v2.InconsistentParameters;
import org.oasis.wsrp.v2.InvalidHandle;
import org.oasis.wsrp.v2.InvalidRegistration;
import org.oasis.wsrp.v2.InvalidUserCategory;
import org.oasis.wsrp.v2.MissingParameters;
import org.oasis.wsrp.v2.OperationFailed;
import org.oasis.wsrp.v2.PortletContext;
import org.oasis.wsrp.v2.PortletDescriptionResponse;
import org.oasis.wsrp.v2.PortletPropertyDescriptionResponse;
import org.oasis.wsrp.v2.PropertyList;
import org.oasis.wsrp.v2.SetPortletProperties;

import javax.jws.HandlerChain;
import javax.jws.WebParam;
import javax.xml.ws.Holder;
import java.util.List;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 8784 $
 * @since 2.4
 */
@javax.jws.WebService(
   name = "WSRPV1PortletManagementPortType",
   serviceName = "WSRPService",
   portName = "WSRPPortletManagementService",
   targetNamespace = "urn:oasis:names:tc:wsrp:v1:wsdl",
   wsdlLocation = "/WEB-INF/wsdl/wsrp_services.wsdl",
   endpointInterface = "org.oasis.wsrp.v1.WSRPV1PortletManagementPortType"
)
@HandlerChain(file = "wshandlers.xml")
public class PortletManagementEndpoint extends WSRPBaseEndpoint implements WSRPV1PortletManagementPortType
{
   public void getPortletPropertyDescription(
      @WebParam(name = "registrationContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1RegistrationContext registrationContext,
      @WebParam(name = "portletContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1PortletContext portletContext,
      @WebParam(name = "userContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1UserContext userContext,
      @WebParam(name = "desiredLocales", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") List<String> desiredLocales,
      @WebParam(mode = WebParam.Mode.OUT, name = "modelDescription", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<V1ModelDescription> modelDescription,
      @WebParam(mode = WebParam.Mode.OUT, name = "resourceList", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<V1ResourceList> resourceList,
      @WebParam(mode = WebParam.Mode.OUT, name = "extensions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1Extension>> extensions
   ) throws V1MissingParameters, V1InconsistentParameters, V1InvalidHandle, V1InvalidRegistration, V1InvalidUserCategory, V1AccessDenied, V1OperationFailed
   {
      GetPortletPropertyDescription getPortletPropertyDescription = new GetPortletPropertyDescription();
      getPortletPropertyDescription.setRegistrationContext(V1ToV2Converter.toV2RegistrationContext(registrationContext));
      getPortletPropertyDescription.setPortletContext(V1ToV2Converter.toV2PortletContext(portletContext));
      getPortletPropertyDescription.setUserContext(V1ToV2Converter.toV2UserContext(userContext));
      getPortletPropertyDescription.getDesiredLocales().addAll(desiredLocales);
      PortletPropertyDescriptionResponse descriptionResponse;
      try
      {
         descriptionResponse = producer.getPortletPropertyDescription(getPortletPropertyDescription);

         modelDescription.value = V2ToV1Converter.toV1ModelDescription(descriptionResponse.getModelDescription());
         resourceList.value = V2ToV1Converter.toV1ResourceList(descriptionResponse.getResourceList());
         extensions.value = WSRPUtils.transform(descriptionResponse.getExtensions(), V2ToV1Converter.EXTENSION);
      }
      catch (AccessDenied accessDenied)
      {
         WSRP1ExceptionFactory.throwWSException(accessDenied.getClass(), accessDenied.getMessage(), accessDenied);
      }
      catch (InvalidHandle invalidHandle)
      {
         WSRP1ExceptionFactory.throwWSException(invalidHandle.getClass(), invalidHandle.getMessage(), invalidHandle);
      }
      catch (InvalidUserCategory invalidUserCategory)
      {
         WSRP1ExceptionFactory.throwWSException(invalidUserCategory.getClass(), invalidUserCategory.getMessage(), invalidUserCategory);
      }
      catch (InconsistentParameters inconsistentParameters)
      {
         WSRP1ExceptionFactory.throwWSException(inconsistentParameters.getClass(), inconsistentParameters.getMessage(), inconsistentParameters);
      }
      catch (MissingParameters missingParameters)
      {
         WSRP1ExceptionFactory.throwWSException(missingParameters.getClass(), missingParameters.getMessage(), missingParameters);
      }
      catch (InvalidRegistration invalidRegistration)
      {
         WSRP1ExceptionFactory.throwWSException(invalidRegistration.getClass(), invalidRegistration.getMessage(), invalidRegistration);
      }
      catch (OperationFailed operationFailed)
      {
         WSRP1ExceptionFactory.throwWSException(operationFailed.getClass(), operationFailed.getMessage(), operationFailed);
      }
   }

   public void setPortletProperties(
      @WebParam(name = "registrationContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1RegistrationContext registrationContext,
      @WebParam(name = "portletContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1PortletContext portletContext,
      @WebParam(name = "userContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1UserContext userContext,
      @WebParam(name = "propertyList", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1PropertyList propertyList,
      @WebParam(mode = WebParam.Mode.OUT, name = "portletHandle", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<String> portletHandle,
      @WebParam(mode = WebParam.Mode.OUT, name = "portletState", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<byte[]> portletState,
      @WebParam(mode = WebParam.Mode.OUT, name = "extensions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1Extension>> extensions
   ) throws V1MissingParameters, V1InconsistentParameters, V1InvalidHandle, V1InvalidRegistration, V1InvalidUserCategory, V1AccessDenied, V1OperationFailed
   {
      SetPortletProperties setPortletProperties = new SetPortletProperties();
      setPortletProperties.setRegistrationContext(V1ToV2Converter.toV2RegistrationContext(registrationContext));
      setPortletProperties.setPortletContext(V1ToV2Converter.toV2PortletContext(portletContext));
      setPortletProperties.setUserContext(V1ToV2Converter.toV2UserContext(userContext));
      setPortletProperties.setPropertyList(V1ToV2Converter.toV2PropertyList(propertyList));

      PortletContext response;
      try
      {
         response = producer.setPortletProperties(setPortletProperties);
         portletHandle.value = response.getPortletHandle();
         portletState.value = response.getPortletState();
         extensions.value = WSRPUtils.transform(response.getExtensions(), V2ToV1Converter.EXTENSION);
      }
      catch (AccessDenied accessDenied)
      {
         WSRP1ExceptionFactory.throwWSException(accessDenied.getClass(), accessDenied.getMessage(), accessDenied);
      }
      catch (InvalidHandle invalidHandle)
      {
         WSRP1ExceptionFactory.throwWSException(invalidHandle.getClass(), invalidHandle.getMessage(), invalidHandle);
      }
      catch (InvalidUserCategory invalidUserCategory)
      {
         WSRP1ExceptionFactory.throwWSException(invalidUserCategory.getClass(), invalidUserCategory.getMessage(), invalidUserCategory);
      }
      catch (InconsistentParameters inconsistentParameters)
      {
         WSRP1ExceptionFactory.throwWSException(inconsistentParameters.getClass(), inconsistentParameters.getMessage(), inconsistentParameters);
      }
      catch (MissingParameters missingParameters)
      {
         WSRP1ExceptionFactory.throwWSException(missingParameters.getClass(), missingParameters.getMessage(), missingParameters);
      }
      catch (InvalidRegistration invalidRegistration)
      {
         WSRP1ExceptionFactory.throwWSException(invalidRegistration.getClass(), invalidRegistration.getMessage(), invalidRegistration);
      }
      catch (OperationFailed operationFailed)
      {
         WSRP1ExceptionFactory.throwWSException(operationFailed.getClass(), operationFailed.getMessage(), operationFailed);
      }
   }

   public void clonePortlet(
      @WebParam(name = "registrationContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1RegistrationContext registrationContext,
      @WebParam(name = "portletContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1PortletContext portletContext,
      @WebParam(name = "userContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1UserContext userContext,
      @WebParam(mode = WebParam.Mode.OUT, name = "portletHandle", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<String> portletHandle,
      @WebParam(mode = WebParam.Mode.OUT, name = "portletState", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<byte[]> portletState,
      @WebParam(mode = WebParam.Mode.OUT, name = "extensions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1Extension>> extensions
   ) throws V1MissingParameters, V1InconsistentParameters, V1InvalidHandle, V1InvalidRegistration, V1InvalidUserCategory, V1AccessDenied, V1OperationFailed
   {
      ClonePortlet clonePortlet = new ClonePortlet();
      clonePortlet.setRegistrationContext(V1ToV2Converter.toV2RegistrationContext(registrationContext));
      clonePortlet.setPortletContext(V1ToV2Converter.toV2PortletContext(portletContext));
      clonePortlet.setUserContext(V1ToV2Converter.toV2UserContext(userContext));

      PortletContext response;
      try
      {
         response = producer.clonePortlet(clonePortlet);
         portletHandle.value = response.getPortletHandle();
         portletState.value = response.getPortletState();
         extensions.value = WSRPUtils.transform(response.getExtensions(), V2ToV1Converter.EXTENSION);
      }
      catch (AccessDenied accessDenied)
      {
         WSRP1ExceptionFactory.throwWSException(accessDenied.getClass(), accessDenied.getMessage(), accessDenied);
      }
      catch (InvalidHandle invalidHandle)
      {
         WSRP1ExceptionFactory.throwWSException(invalidHandle.getClass(), invalidHandle.getMessage(), invalidHandle);
      }
      catch (InvalidUserCategory invalidUserCategory)
      {
         WSRP1ExceptionFactory.throwWSException(invalidUserCategory.getClass(), invalidUserCategory.getMessage(), invalidUserCategory);
      }
      catch (InconsistentParameters inconsistentParameters)
      {
         WSRP1ExceptionFactory.throwWSException(inconsistentParameters.getClass(), inconsistentParameters.getMessage(), inconsistentParameters);
      }
      catch (MissingParameters missingParameters)
      {
         WSRP1ExceptionFactory.throwWSException(missingParameters.getClass(), missingParameters.getMessage(), missingParameters);
      }
      catch (InvalidRegistration invalidRegistration)
      {
         WSRP1ExceptionFactory.throwWSException(invalidRegistration.getClass(), invalidRegistration.getMessage(), invalidRegistration);
      }
      catch (OperationFailed operationFailed)
      {
         WSRP1ExceptionFactory.throwWSException(operationFailed.getClass(), operationFailed.getMessage(), operationFailed);
      }
   }

   public void getPortletDescription(
      @WebParam(name = "registrationContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1RegistrationContext registrationContext,
      @WebParam(name = "portletContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1PortletContext portletContext,
      @WebParam(name = "userContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1UserContext userContext,
      @WebParam(name = "desiredLocales", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") List<String> desiredLocales,
      @WebParam(mode = WebParam.Mode.OUT, name = "portletDescription", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<V1PortletDescription> portletDescription,
      @WebParam(mode = WebParam.Mode.OUT, name = "resourceList", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<V1ResourceList> resourceList,
      @WebParam(mode = WebParam.Mode.OUT, name = "extensions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1Extension>> extensions
   ) throws V1MissingParameters, V1InconsistentParameters, V1InvalidHandle, V1InvalidRegistration, V1InvalidUserCategory, V1AccessDenied, V1OperationFailed
   {
      GetPortletDescription getPortletDescription = new GetPortletDescription();
      getPortletDescription.setRegistrationContext(V1ToV2Converter.toV2RegistrationContext(registrationContext));
      getPortletDescription.setPortletContext(V1ToV2Converter.toV2PortletContext(portletContext));
      getPortletDescription.setUserContext(V1ToV2Converter.toV2UserContext(userContext));
      getPortletDescription.getDesiredLocales().addAll(desiredLocales);

      try
      {
         PortletDescriptionResponse description = producer.getPortletDescription(getPortletDescription);

         portletDescription.value = V2ToV1Converter.toV1PortletDescription(description.getPortletDescription());
         resourceList.value = V2ToV1Converter.toV1ResourceList(description.getResourceList());
         extensions.value = WSRPUtils.transform(description.getExtensions(), V2ToV1Converter.EXTENSION);
      }
      catch (AccessDenied accessDenied)
      {
         WSRP1ExceptionFactory.throwWSException(accessDenied.getClass(), accessDenied.getMessage(), accessDenied);
      }
      catch (InvalidHandle invalidHandle)
      {
         WSRP1ExceptionFactory.throwWSException(invalidHandle.getClass(), invalidHandle.getMessage(), invalidHandle);
      }
      catch (InvalidUserCategory invalidUserCategory)
      {
         WSRP1ExceptionFactory.throwWSException(invalidUserCategory.getClass(), invalidUserCategory.getMessage(), invalidUserCategory);
      }
      catch (InconsistentParameters inconsistentParameters)
      {
         WSRP1ExceptionFactory.throwWSException(inconsistentParameters.getClass(), inconsistentParameters.getMessage(), inconsistentParameters);
      }
      catch (MissingParameters missingParameters)
      {
         WSRP1ExceptionFactory.throwWSException(missingParameters.getClass(), missingParameters.getMessage(), missingParameters);
      }
      catch (InvalidRegistration invalidRegistration)
      {
         WSRP1ExceptionFactory.throwWSException(invalidRegistration.getClass(), invalidRegistration.getMessage(), invalidRegistration);
      }
      catch (OperationFailed operationFailed)
      {
         WSRP1ExceptionFactory.throwWSException(operationFailed.getClass(), operationFailed.getMessage(), operationFailed);
      }
   }

   public void getPortletProperties(
      @WebParam(name = "registrationContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1RegistrationContext registrationContext,
      @WebParam(name = "portletContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1PortletContext portletContext,
      @WebParam(name = "userContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1UserContext userContext,
      @WebParam(name = "names", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") List<String> names,
      @WebParam(mode = WebParam.Mode.OUT, name = "properties", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1Property>> properties,
      @WebParam(mode = WebParam.Mode.OUT, name = "resetProperties", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1ResetProperty>> resetProperties,
      @WebParam(mode = WebParam.Mode.OUT, name = "extensions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1Extension>> extensions
   ) throws V1MissingParameters, V1InconsistentParameters, V1InvalidHandle, V1InvalidRegistration, V1InvalidUserCategory, V1AccessDenied, V1OperationFailed
   {
      GetPortletProperties getPortletProperties = new GetPortletProperties();
      getPortletProperties.setRegistrationContext(V1ToV2Converter.toV2RegistrationContext(registrationContext));
      getPortletProperties.setPortletContext(V1ToV2Converter.toV2PortletContext(portletContext));
      getPortletProperties.setUserContext(V1ToV2Converter.toV2UserContext(userContext));
      getPortletProperties.getNames().addAll(names);

      try
      {
         PropertyList result = producer.getPortletProperties(getPortletProperties);

         properties.value = WSRPUtils.transform(result.getProperties(), V2ToV1Converter.PROPERTY);
         resetProperties.value = WSRPUtils.transform(result.getResetProperties(), V2ToV1Converter.RESETPROPERTY);
         extensions.value = WSRPUtils.transform(result.getExtensions(), V2ToV1Converter.EXTENSION);
      }
      catch (AccessDenied accessDenied)
      {
         WSRP1ExceptionFactory.throwWSException(accessDenied.getClass(), accessDenied.getMessage(), accessDenied);
      }
      catch (InvalidHandle invalidHandle)
      {
         WSRP1ExceptionFactory.throwWSException(invalidHandle.getClass(), invalidHandle.getMessage(), invalidHandle);
      }
      catch (InvalidUserCategory invalidUserCategory)
      {
         WSRP1ExceptionFactory.throwWSException(invalidUserCategory.getClass(), invalidUserCategory.getMessage(), invalidUserCategory);
      }
      catch (InconsistentParameters inconsistentParameters)
      {
         WSRP1ExceptionFactory.throwWSException(inconsistentParameters.getClass(), inconsistentParameters.getMessage(), inconsistentParameters);
      }
      catch (MissingParameters missingParameters)
      {
         WSRP1ExceptionFactory.throwWSException(missingParameters.getClass(), missingParameters.getMessage(), missingParameters);
      }
      catch (InvalidRegistration invalidRegistration)
      {
         WSRP1ExceptionFactory.throwWSException(invalidRegistration.getClass(), invalidRegistration.getMessage(), invalidRegistration);
      }
      catch (OperationFailed operationFailed)
      {
         WSRP1ExceptionFactory.throwWSException(operationFailed.getClass(), operationFailed.getMessage(), operationFailed);
      }
   }

   public void destroyPortlets(
      @WebParam(name = "registrationContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1RegistrationContext registrationContext,
      @WebParam(name = "portletHandles", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") List<String> portletHandles,
      @WebParam(mode = WebParam.Mode.OUT, name = "destroyFailed", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1DestroyFailed>> destroyFailed,
      @WebParam(mode = WebParam.Mode.OUT, name = "extensions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1Extension>> extensions
   ) throws V1MissingParameters, V1InconsistentParameters, V1InvalidRegistration, V1OperationFailed
   {
      DestroyPortlets destroyPortlets = new DestroyPortlets();
      destroyPortlets.setRegistrationContext(V1ToV2Converter.toV2RegistrationContext(registrationContext));
      destroyPortlets.getPortletHandles().addAll(portletHandles);

      DestroyPortletsResponse destroyPortletsResponse;
      try
      {
         destroyPortletsResponse = producer.destroyPortlets(destroyPortlets);

         destroyFailed.value = V2ToV1Converter.toV1DestroyFailed(destroyPortletsResponse.getFailedPortlets());
         extensions.value = WSRPUtils.transform(destroyPortletsResponse.getExtensions(), V2ToV1Converter.EXTENSION);
      }
      catch (InconsistentParameters inconsistentParameters)
      {
         WSRP1ExceptionFactory.throwWSException(inconsistentParameters.getClass(), inconsistentParameters.getMessage(), inconsistentParameters);
      }
      catch (MissingParameters missingParameters)
      {
         WSRP1ExceptionFactory.throwWSException(missingParameters.getClass(), missingParameters.getMessage(), missingParameters);
      }
      catch (InvalidRegistration invalidRegistration)
      {
         WSRP1ExceptionFactory.throwWSException(invalidRegistration.getClass(), invalidRegistration.getMessage(), invalidRegistration);
      }
      catch (OperationFailed operationFailed)
      {
         WSRP1ExceptionFactory.throwWSException(operationFailed.getClass(), operationFailed.getMessage(), operationFailed);
      }
   }
}
