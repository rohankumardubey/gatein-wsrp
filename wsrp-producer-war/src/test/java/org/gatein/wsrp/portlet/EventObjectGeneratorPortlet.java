/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2010, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.gatein.wsrp.portlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.namespace.QName;

import org.gatein.wsrp.portlet.utils.TestObject;

/**
 * EventObjectGenerator Portlet Class
 */
public class EventObjectGeneratorPortlet extends GenericPortlet 
{
	
	private List<TestObject> objects;
	
	@Override
	public void init() throws PortletException 
	{
		super.init();
		objects = new ArrayList<TestObject>();
		objects.add(new TestObject("Prabhat", "Jha", "pjha", 654321, "pjha@redhat.com"));
		objects.add(new TestObject("Michal", "Vanco", "mvanco", 123456, "mvanco@redhat.com"));
		objects.add(new TestObject("Marek", "Posolda", "mposolda", 112233, "mposolda@redhat.com"));
		objects.add(new TestObject("Viliam", "Rockai", "vrockai", 223311, "vrockai@redhat.com"));
	}
	
	public TestObject getTestObjectByUserName(String username) 
	{
		for (TestObject object : objects) 
		{
			if (object.getUsername().equals(username)) 
			{
				return object;
			}
		}
		return null;
	}

	@Override
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException 
	{
		response.setContentType("text/html");
		request.setAttribute("objects", objects);
		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/view_generator.jsp");
		dispatcher.include(request, response);
	}

	@Override
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException 
	{
		String username = request.getParameter("username");
		response.setEvent(new QName("urn:jboss:gatein:samples:event:object", "eventObject"), getTestObjectByUserName(username));
	}
}
