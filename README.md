rs-instance-jumpstart
=====================

Simple Graphical Java tool to start Rackspace Next Gen / OpenStack Cloud Servers in Shutoff state

# Installation and use:

For normal use, just hit the 'download as zip' button, save it somewhere you'll know how to get to,
and unzip it. You won't need the whole project, only the 'dist' folder, but the full project is
pretty small anyway. The dist folder contains the latest working build as a .jar file, plus a folder
containing libs it depends on. There are two additional files, rs-instance-jumpstart.bat (for
Windows) and rs-instance-jumpstart.sh (For Mac and Linux), which simply contain the command to run
this app in Java. To 'install', you'll just need to download these files and put them in a folder
you can get to later. If you don't have Java installed, that will be required as well, and you can
find it at http://java.com/en/

As long as you have a recent version of Java installed, you should be able to simply run
RSInstanceJumpstart.jar directly. If not, there are a few more methods you can try. To run the app
in Windows, double-click or otherwise run rs-instance-jumpstart.bat. For Mac and Linux, run
rs-instance-jumpstart.sh instead. You can also run it from a command line with this command:

java -jar RSInstanceJumpstart.jar

The app will simply prompt for your Rackspace username and API key, and use these to authenticate
and display a list of servers (if any) that are currently in 'Shutoff' state. Each of these has a
button you can click to turn the server on. It's recommended to do this for one at a time. When
clicked, the request will be sent and then the app will watch for 30 seconds to see if the server
returns to the proper 'Active' state. Most will complete within this time. If this times out, it
does not necessarily mean your server is not in the process of powering on, so it is recommended in
such cases to watch it for a few minutes in the Open Cloud Control Panel. You can also click the
menu in rs-instance-jumpstart and re-login to refresh the server list within the app. If a server
stays in 'Shutoff' state for more than a few minutes, or goes into an error state, it's recommended
to contact Rackspace Cloud support via ticket, chat, or phone.

# Questions and Answers

Q: Why create this tool?

A: Because if a Next Gen Rackspace server is in 'Shutoff' state, at time of writing the Cloud Control
Panel offered no way to turn it on - for First Gen servers you can issue a hard reboot to do it. Next
Gen (OpenStack) servers can be hard rebooted from the API to turn them back on, but many customers
either cannot or would prefer not to use the API in this way.

Q: Who is the intended audience?

A: Primarily, this is geared for non-technical users who would benefit from a way to power servers
that are in 'Shutoff' state back on that doens't require command line use or scripting. The majority
of these are likely to be Windows users, for a few reasons. One is that you can set up and manage
a Windows Cloud Server without ever needing to use a command line interface, unlike Linux servers.
Another is that Windows desktop users are familiar with shutting down their machine after use, and there
are many Rackspace customers using their Windows servers as a shared desktop for application like
QuickBooks - to compund the issue, it is a common misconception that halted servers do not incur
hourly charges. Finally, there are several kinds of errors that can cause a Windows server to
spontaneously halt, and then enter 'Shutoff' state.

Q: Will this tool allow me to intentionally power off servers so they won't cost anything, and then
power then back on again when I need them? Rackspace's site says I'll only be charged for what I
use.

A: As noted above, this is a common misunderstanding - as long as a server builds correctly, it will
continue to incur the hourly usage charge until it is deleted, whether it is running or not. The
statement that you only pay for what you use is in reference to the fact that pricing for Cloud
Servers is a utility pricing model - if your server only existed for 8 hours you only pay for
8 hours, not a whole month, and your bandwidth is simply metered and billed, like water or
electricity. If you'd like to set aside a server and not be charged for it, and then power it back
on later for use, the closest method we currently have is to create an image the server (and then
run a test build to ensure viability of the image), delete the server, and then recreate it when
needed from that tested image, which is stored in Cloud Files. Using this method, the only thing
that would change is the IP addresses allocated to it, and the root or Administrator password would
be reset.

Q: Why did you use Java?

A: due to the intended audience, this tool had to have a simple graphical interface, and it had to
run on multiple platforms, including Windows. As a Linux SysAdmin, Java is probably the closest I
will ever get to coding for Windows GUIs.

Q: It's asking for my API Key. Where do I find that?

A: Your API Key could be though of as a second password on your account for API operations. You can
find it in the Rackspace Open Cloud Control Panel by clicking your username and account number in
the upper right corner, and then clicking 'API Keys' in the resulting drop-down menu.

Q: Isn't this project going to become obsolete as soon as the Open Cloud Control Panel is updated
to not have the 'Reboot server' action grayed out when in 'Shutoff' state?

A: That's the idea. But I'm also making it for the purpose of gaining experience and having a good
framework to build other such apps in the future. To this end I've licensed it under the LGPL,
so others can use it a starting point for their own API-aware Java apps as well.
