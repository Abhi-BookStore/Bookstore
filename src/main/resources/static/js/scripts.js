/**
 * 
 */
function validateConfirmPassword(){
	var password = $('#txtNewPasssword').val();
	var confirmPassword = $('#txtConfirmPasssword').val();
	
	if(password == "" && confirmPassword == ""){
		$('#checkPasswordMatch').html("");
		$('#updateUserInfoButton').prop("disabled", false);
	}else{
		if(password != confirmPassword){
			$('#checkPasswordMatch').html("Password do not match.");
			$('#updateUserInfoButton').prop("disabled", true);
		} else {
			$('#checkPasswordMatch').html("Password matched.");
			$('#updateUserInfoButton').prop("disabled", false);
		}
	}
}


function checkBillingAddress(){
	if($('#theSameAsShippingAddress').is(":checked")){
		$('.billingAddress').prop("disabled", true);
	}else{
		$('.billingAddress').prop("disabled", false);
	}
}

$(document).ready(function(){
	$('.cartItemQty').on('change', function(){
		var id = this.id;
		$('#update-item-'+id).css('display', 'inline-block');
	});
	$('#theSameAsShippingAddress').on('click', checkBillingAddress);
	$('#txtConfirmPasssword').keyup(validateConfirmPassword);
	$('#txtNewPasssword').keyup(validateConfirmPassword);
});