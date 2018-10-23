///////////////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2013 Cranefield S., Ranathunga S. All rights reserved.               /
// ---------------------------------------------------------------------------------- /
// This file is part of camel_jason.                                                  /

//    camel_jason is free software: you can redistribute it and/or modify             /
//   it under the terms of the GNU Lesser General Public License as published by      /
//    the Free Software Foundation, either version 3 of the License, or               /
//    (at your option) any later version.                                             /

//    camel_jason is distributed in the hope that it will be useful,                  /
//    but WITHOUT ANY WARRANTY; without even the implied warranty of                  /
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   /
//    GNU Lesser General Public License for more details.                             /

//    You should have received a copy of the GNU Lesser General Public License        /
//    along with camel_jason.  If not, see <http://www.gnu.org/licenses/>.            /
///////////////////////////////////////////////////////////////////////////////////////

package camelagent;

import jason.asSemantics.Message;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

/**
 * @author surangika Contains the Jason-specific implementation of the Camel
 *         producer component
 */
public class AgentProducer extends DefaultProducer {
	AgentEndpoint endpoint;
	AgentComponent bdi_component;

	public AgentProducer(AgentEndpoint endpoint, AgentComponent bdi_component) {
		super(endpoint);
		this.endpoint = endpoint;
		this.bdi_component = bdi_component;
	}

	public void process(Exchange exchange) throws Exception {
		String ei = endpoint.getIlloc_force();
		String es = endpoint.getSender();
		String er = endpoint.getReceiver();
		String ea = endpoint.getAnnotations();

		// extract header
		Map<String, Object> headerInfo = exchange.getIn().getHeaders();

		// A BDI producer receives either "message" or "percept"
		if (endpoint.getUriOption().contains("message")) {
			String content = exchange.getIn().getBody(String.class);
			String r = "";
			String s = "";
			String i = "";
			String annots = "";
			if (headerInfo.containsKey("annotations"))
				annots = (String) headerInfo.get("annotations");
			if (headerInfo.containsKey("receiver"))
				r = (String) headerInfo.get("receiver");
			else
				throw new IllegalArgumentException("Must specify receiver");
			if (headerInfo.containsKey("sender"))
				s = (String) headerInfo.get("sender");
			else
				throw new IllegalArgumentException("Must specify sender");
			if (headerInfo.containsKey("illoc_force"))
				i = (String) headerInfo.get("illoc_force");
			else if (ei != null)
				i = ei;
			else
				throw new IllegalArgumentException("Must specify illocutionary force");

			if ((i.equals(ei) || ei == null || i == null) && (s.equals(es) || es == null)
					&& (annots.equals(ea) || ea == "")) {
				Matcher matcher = endpoint.getBodyMatcher(content);

				if (((r.equals(er) || er == null))) {
					sendMatchedMessagetoJason(matcher, content, r, i, s, annots);
				}
			}
		}
		if (endpoint.getUriOption().contains("percept")) {
			Object body = exchange.getIn().getBody();
			Collection<Literal> content;
			if (body instanceof Collection) {
				Collection origContent = (Collection) body;
				content = new ArrayList<Literal>();
				for (Object item : origContent) {
					Literal lit = exchange.getContext().getTypeConverter().convertTo(Literal.class, item);
					content.add(lit);
				}
			} else {
				// Single percept in body
				// Get existing literal or use type converter class LiteralConverter

				Literal lit = exchange.getIn().getBody(Literal.class);

				content = Collections.singleton(lit);
			}
			String r = "";
			String persistent = endpoint.getPersistent();
			String updateMode = endpoint.getUpdateMode();
			Set<String> nafFunctors = endpoint.getNafFunctors();
			String annots = endpoint.getAnnotations();
			// TO DO: add support for receiver being a comma-separated list (as promised in
			// EMAS'13 paper)
			if (headerInfo.containsKey("receiver"))
				r = (String) headerInfo.get("receiver");
			if (headerInfo.containsKey("annotations"))
				annots = (String) headerInfo.get("annotations");
			if (headerInfo.containsKey("persistent"))
				persistent = (String) headerInfo.get("persistent");
			if (headerInfo.containsKey("updateMode"))
				updateMode = (String) headerInfo.get("updateMode");
			if (headerInfo.containsKey("naf")) {
				nafFunctors = endpoint.getNafFunctors((String) headerInfo.get("naf"));
			}
			if (er == null || r.equals(er)) {
				this.bdi_component.getContainer().getCamelpercepts(content, r, annots, updateMode, nafFunctors,
						persistent);
			}
		}
	}

	/**
	 * @param matcher
	 * @param content
	 * @param receiver
	 * @param illoc
	 * @param sender
	 * @param annotations
	 *            Sends the message to the agent if the message content matches with
	 *            the uri information
	 */
	private void sendMatchedMessagetoJason(Matcher matcher, String content, String receiver, String illoc,
			String sender, String annotations) {
		String matchedS = endpoint.getReplacedContent(matcher, content);

		matchedS = matchedS.replace("\n", "").replace("\r", "");

		Message m;
		try {
			m = new Message(illoc, sender, receiver, ASSyntax.parseTerm(matchedS));

			List<String> annots = Arrays.asList(annotations.split(","));
			Literal lit = null;
			Object propCont = m.getPropCont();
			if (propCont instanceof Literal) {
				lit = (Literal) propCont;
			}
			// Add the separately received annotations to the literal
			if (lit != null && annots != null) {
				for (String as : annots) {
					if (!as.equals("")) {
						lit.addAnnot(ASSyntax.parseTerm(as));
					}
				}
			}

			if (matcher == null) {
				this.bdi_component.getContainer().getCamelMessages(m, receiver);
			} else if (matcher.find())
				this.bdi_component.getContainer().getCamelMessages(m, receiver);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
