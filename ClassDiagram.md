# Class Diagram #

_This page introduces layout of class diagram for [DesKal](ProjectSummary.md)._

![https://deskal.googlecode.com/svn/image/DesKalClassDiagram4.jpg](https://deskal.googlecode.com/svn/image/DesKalClassDiagram4.jpg)

# Notes #

  * Project should contain 3 java Interfaces and 6 Classes.
  * For all Manager Interfaces there will be ManagerImpl Classes implementing them.
  * Classes representing actual objects (i.e. Event, Day and Filter) will have get() and set() methods for all their attributes.
  * Methods are called by Events from GUI.

  * Java data types XMLGregorianCalendar and Duration are binding types for XML Schema types date and duration respectivly.
