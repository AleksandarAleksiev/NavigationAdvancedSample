I created 2 different branches to test different technologies

In the master branch I used android nav component with custom back nav handling
Few things I've learned while looking at few back stack navigation scenarios with the nav components
First thing is that with global actions you can't navigate to a screen that is deep in another nav graph. You can only navigate to the start destination of a graph. For this case you have to use deeplinks but I think there's a bug with the deeplink implementation as it correctly navigates the user to the correct screen but then when switch to another nav graph (bottom navigation tab) then the stack is not restored correctly. Same happens if the app was closed and you start it from a deep-link
With the default implementation by Google if the user is navigated deeply in the main back stack and then switches bottom tabs and navigates deeply in the second back stack and then the user navigates all the way back to the root of that graph, on next back button click the user is always navigated back to the main bottom tab but always to the root screen of that main back stack 

In the FragmentTransaction branch I used the new FragmentManager API saveBackStack and restoreBackStack
https://medium.com/androiddevelopers/multiple-back-stacks-b714d974f134
https://developer.android.com/guide/fragments/fragmentmanager

