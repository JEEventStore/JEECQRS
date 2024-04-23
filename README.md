# Note

A decade later and having developed several message driven applications later, I would probably implement a few things quite a bit differently today.  I'm no longer maintaining this and merely keep it as a reference implementation for other people to learn about event sourcing and investigate. 

JEECQRS
=======

JEECQRS aims to provide a suitable set of low-level infrastructure
services that are needed in any CQRS implementation.  It uses the
infrastructure provided by any JEE 6.0 (web-profile or full profile
for JMS messaging) compliant container.

It is recommended (but not necessary) to hide these infrastructure
services behind suitable [Facades](http://c2.com/cgi/wiki?FacadePattern)
in your own application.

Tested containers:
* JBoss AS 7.x
* Wildfly 8.2 (8.1 is not supported due to bugs in WELD)
* Wildfly 9.0 (8.1 is not supported due to bugs in WELD)
