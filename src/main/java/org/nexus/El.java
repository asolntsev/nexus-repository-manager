package org.nexus;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class El {
  private final Element element;

  private El(Element element) {
    this.element = element;
  }

  public static El root(Document document) {
    return new El(document.getDocumentElement());
  }
  
  public long longValue() {
    return Long.parseLong(element.getTextContent());
  }

  public String text() {
    return element.getTextContent();
  }

  public El singleElement(String tag) {
    NodeList nodes = element.getElementsByTagName(tag);
    if (nodes.getLength() == 0) {
      throw new IllegalArgumentException("Node not found: " + element.getNodeName() + "/" + tag);
    }
    if (nodes.getLength() > 1) {
      throw new IllegalArgumentException("Multiple child nodes found: " + element.getNodeName() + "/" + tag);
    }
    return new El((Element) nodes.item(0));
  }
  
  public <Result> List<Result> children(String tag, Function<El, Result> mapper) {
    NodeList childNodes = element.getElementsByTagName(tag);
    List<Result> results = new ArrayList<>(childNodes.getLength());
    for (int i = 0; i < childNodes.getLength(); i++) {
      Element node = (Element) childNodes.item(i);
      results.add(mapper.apply(new El(node)));
    }
    return results;
  }
}
