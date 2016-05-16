ThousandSmilesRegister
======================

An Android application used by volunteers who have registered for a Thousand
Smiles clinic to sign in and generate a badge. The application uses a RESTful
API supported by our database backend to obtain information about the current
clinic, gets a list of volunteers using the same backend and their basic
registration data, including a PIN number. The user selects the first letter
of their last name, then a screen displays the registrants who match. They
then select their name, and are prompted for a PIN number which was generated
when they registered. If they successfully enter the PIN, the RESTful API is
used to generate a badge image in PNG format.

The application then uses the Zequs service (https://github.com/slogan621/zequs)
which is running on a server to print the badge.

Build Notes
-----------

ThousandSmilesRegister is designed for development in Android Studio. 
Install Android Studio on your system, git clone this repo, then "open"
the project by clicking on the folder that was cloned from github.

You will need to edit the file app/src/main/java/org/thousandsmiles/thousandsmilesregister/RESTful.java and change m_dbAPIToken and m_dbAPIHost as instructed
in the file to point at the DB backend RESTful API.

Supported Android Versions and Tablets
--------------------------------------

The software should run on Android 4.4 or later.

The software layout is somewhat hardcoded for larger screened Android Tablets,
and is known to work with Google's Nexus 9 and Samsung Galaxy Tab 4 (which we
us in our clinics).


RESTful API
-----------

The application makes use of two RESTful APIs. One is defined by the software
that implements our volunteer database backend, and is used to get info about
clinics, as well as volunteer lists. perform the sign in function, and generate
a badge image (PNG format). 

The other RESTful API is directed towards Zequs which handles printing of the
ID cards.

The DB RESTful API can be deduced from reading the code, at this point, the
API is undocumented.

The RESTful API for Zequs (which handles the printing) is located at the
following URL: https://github.com/slogan621/zequs/blob/master/docs/rest-api.md
