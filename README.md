# LTT
Lenovo Test Tools

<h3>Link to .apk file</h3>
<a href="https://onedrive.live.com/redir?resid=4E4659BC89F6BE5C!248&authkey=!AMyk1eomhFOs7WY&ithint=file%2capk">new version with lollipop support</a>
<font color="red">On lollipop you need to turn on/off data manually for each test item (it's because android.permission.CHANGE_NETWORK_STATE in new API was blocked for non-system apps)</font>


<h2>Description</h2>
Application for doing some field tests, for now there is only Call test support.

<h3>Call test</h3>
<img src="https://github.com/trzye/LTT/blob/master/calltest_new.jpeg" height="640" width="360"><br>
Before using this test tool please set data connection to 3g or 4g and turn off WiFi connection.
In application you can set "PASS time (seconds)", if DUT didn't connect to the Internet until this time it will be marked as FAIL.
You can set "Number of tests" also, it means how many test you would like to do.
After test you can save results to clipboard and after that copy it to your favourite note application. Enjoy! :) <br><br>
In few words app is simply waiting for 200 code status from http://google.com/ after turning off/on data connection.
