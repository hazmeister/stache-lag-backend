# stache-lag-backend
A technical challenge for prospective backend developers at Rock 7 / YB Tracking


1.  <del>Parse the JSON document; it contains positional data "moments" from one of our transatlantic yacht races. You can visualise it here: https://yb.tl/arc2017</del>

2.  <del>Populate this information appropriately into a MySQL database for subsequent processing.</del>

3.  Decide upon an effective method to determine the number of other vessels “visible” for a given moment. e.g. for each moment, decide the number of other vessels that would have been seen by a crew member on that vessel at that position/time (make any appropriate assumptions you need to).

4.  Output a summary table showing average number of sightings per vessel, per day.  For example, it should be possible to make conclusions such as "On day 6 of the rally, a vessel is likely to see 5 other vessels during the day" and "On the day before they finish, a typical vessel is likely to see 4 other vessels".

We're looking for efficiency, readability and correctness.  Extra points if you can give us some other interesting insight into the data that we don't already know!

Send us your resulting table, and your code, or better still, push it to a public git repo and send us a link.  Good luck!
