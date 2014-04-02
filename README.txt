#README.txt#
Clay Comer

Main Activity
	1) Main activity is a web browser that is functional, however to load webpages the checkbox in the setting labled Http Add must 
	be checked.
	2) In action bar, Bookmarks will take the user to the bookmarks activity, Settings will take users to the settings activity, the
	+ button will add the current webpage to the bookmarks database, and the <- acts as the applications back button.
	3) The home button will load the set webpage for the homepage. Default is www.google.com, can be changed in settings Homepage list
	object
	
Bookmarks Activity
	1) Bookmarks Activity implements an sqlite database that stores the web address of saved bookmarks. Users can enter a manual 
	bookmark with the Insert button and delete bookmarks by typing a bookmarks number in the Bookmark Numer edittext box and clicking
	the delete button. A click on any shown bookmarks will load the bookmark in the Main Activity page. Done button will return the user
	the the Main Activity, and the <- button is another implementation of the applications back button.
	
Settings Activity
	1) The homepage menu object allows the user to set the webview's home page
	2) The Mute Volume checkbox will mute all volume on the app when checked
	3) Http adds the http:// to a web address in the Main Activity if checked
	4) Sort bookmarks will sort the bookmarks in the sqllite database when checked by alphabetical order
	5) The Set Time menu object allows the user to set a time for an android notification to appear
	6) The Web Page menu object allows the user to set the webpage for the notification that is clickable and will load in the Main
	Activity.