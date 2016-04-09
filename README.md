# GRTLiveTimes

This is a very simplistic Android app that will display the live times of GRT buses coming to particular stops.

You must enter a stop number.
You can optionally add a route number.

The output is all the live times that buses come to the stop.
If a route number is used, it will filter the buses to be only of that route.

The data is acquired via a web API (uses internet).

You can also add favourites. The favourites are entered into an SQLite database and displayed on a ListView.
The favourites list will always be displayed on screen, so a quick click on one of them will show all the live times for the stop in that favourite.

The UI is barebones. Feel free to contribute to the UI development.