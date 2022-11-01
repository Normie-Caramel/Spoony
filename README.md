# COMP90018_Final_Project_Android

### Git Rules:

1.  After initial setup, any further modification should not be implemented on main branch directly. Instead, please checkout a private branch from ```dev``` branch by:

   ```git 
   git checkout -b [branch name] dev # create a new local branch and switch to it
   git push origin [branch name] # push your local branch to remote repo (optional)
   ```
   
2. Now you can work on your private branch. Once you finish some features and want to share the progress, please pull latest repo and solve conflicts locally, then merge it to dev branch.

   ```git 
   git checkout dev
   git pull origin dev # update the latest dev progress
   git checkout [branch name]
   git merge dev # merge the progress to your private branch
   
   # solve some conflict here
   
   git add .
   git merge --continue # should be no conflict here
   git checkout dev
   git merge [branch name] # update your progress to dev branch
   git push origin dev # push the modification to remote repo
   ```


### Compile Spoony App
1. Open ./Spoony folder in Android Studio and run / build the app.


