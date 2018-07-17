# StorePoint (SP) functionality for books

=> We have to create a functionality where some book contains storepoints.
=> Every SP should have value equal to 1 INR. ie (1 SP = 1 INR)
=> Store point value will be 2.5% of the book price and it should be positive whole number.
=> With every purchase of that particular book, SP should be added to that user's wallet.
=> If user cancelled the order, SP should be reverted.

=> MyAccount Section contain SP wallet. Which should show the earned SP.
=> User have choice to pay through SP with next purchase.


=> There should be history showing all the earned SPs and through which book purchase.


##==================================================================================

##SitePoint class should contain- 

Long id,
Long points,
Long Book,
Long convertedAmount,
Long order#
