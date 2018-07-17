# We have to create functionality for "notify me" when available.

## Option 1:
For this we will add a async method on controller where book is added.

## Option 2:
Create a Scheduler which should run at every 2 hours and triggers the email to all the subscribed customers.
Once the email is triggered, should be marked as sent and on next run it shouldn't trigger for the same user.