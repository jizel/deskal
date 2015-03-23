# XML Schema #

XML Schema for calendar and tags. See tags explanation below.

### Schema contains: ###

  * **labels** - contains labels
    * **label** - _name_ - name of the label, key for tag, unique

  * **event** - node for each event in the calendar
    * _id_ - unique number specifying event
    * **dateSince** - date of the event
    * **dateTo** - end date of the event
    * **timeFrom** - starting time of the event
    * **timeTo** - ending time of the event
    * **title** - title or name of the event
      * _at least one letter is required_
    * **place** - place of event
      * _optional node_
    * **note** - node for some notes about the event
      * _optional node_
    * **tag** - name of the tag (label)
      * _tagref_ - keyref of the label

> As you can see below, uniqueness of name of tag is achieved by setting attribute _name_ of the element _label_ as unique. It is also set as key for attribute _tagref_ of element tag.

> Valid XML document according this schema contains one root element _calendar_. It has to contain one element _labels_ and non or many elements _event_. Element _labels_ can contain undbounded number of elements _label_ with attribute _name_, which has to be unique.

> Element _event_ has exactly one attribute _id_, used for unique identification of the event. Further, element _event_ contains elements used to store information about title, place, note, date of beginning, date of ending. Also contains element _tag_ with attribute _tagref_ which contains reference to the attribute _name_ oh the _label_ element.

## Schema code: ##
```

<?xml version="1.0" encoding="UTF-8"?>

<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified">

<xsd:element name="calendar">
<xsd:complexType>
<xsd:sequence>
<xsd:element name="labels" type="userTypeTag"/>
<xsd:element name="event" minOccurs="0" maxOccurs="unbounded" type="userTypeEvent" />


Unknown end tag for &lt;/sequence&gt;




Unknown end tag for &lt;/complexType&gt;


<xsd:unique name="eventID">
<xsd:selector xpath="event"/>
<xsd:field xpath="@id"/>


Unknown end tag for &lt;/unique&gt;


<xsd:unique name="tag">
<xsd:selector xpath="labels/label"/>
<xsd:field xpath="text"/>


Unknown end tag for &lt;/unique&gt;


<xsd:key name="tagKey">
<xsd:selector xpath="labels/label" />
<xsd:field xpath="@name" />


Unknown end tag for &lt;/key&gt;


<xsd:keyref name="tagKeyRef" refer="tagKey">
<xsd:selector xpath="event/tag"/>
<xsd:field xpath="@tagref"/>


Unknown end tag for &lt;/keyref&gt;




Unknown end tag for &lt;/element&gt;



<xsd:element name="tag" type="xsd:string"/>

<xsd:complexType name="userTypeEvent">
<xsd:sequence>
<xsd:element name="dateSince" type="xsd:date"/>
<xsd:element name="dateTo" type="xsd:date"/>
<xsd:element name="timeFrom" type="xsd:time"/>
<xsd:element name="timeTo" type="xsd:time"/>
<xsd:element name="title" type="userTypeTitle" />
<xsd:element name="place" type="xsd:string" minOccurs="0"/>
<xsd:element name="note" type="xsd:string" minOccurs="0"/>
<xsd:element name="tag" minOccurs="0" maxOccurs="1" type="userTypeTags"/>


Unknown end tag for &lt;/sequence&gt;


<xsd:attribute name="id" type="xsd:string" use="required"/>


Unknown end tag for &lt;/complexType&gt;



<xsd:complexType name="userTypeTag">
<xsd:sequence>
<xsd:element name="label" minOccurs="0" maxOccurs="unbounded">
<xsd:complexType>
<xsd:attribute name="name" type="xsd:NCName"  use="required"/>


Unknown end tag for &lt;/complexType&gt;




Unknown end tag for &lt;/element&gt;




Unknown end tag for &lt;/sequence&gt;




Unknown end tag for &lt;/complexType&gt;



<xsd:simpleType name="userTypeTitle">
<xsd:restriction base="xsd:string">
<xsd:minLength value="1" />


Unknown end tag for &lt;/restriction&gt;




Unknown end tag for &lt;/simpleType&gt;



<xsd:complexType name="userTypeTags">
<xsd:attribute name="tagref" type="xsd:NCName" use="required"/>


Unknown end tag for &lt;/complexType&gt;




Unknown end tag for &lt;/schema&gt;


```