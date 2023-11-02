<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../login/css/login.css" type="text/css" />
</head>
<div id="container" class="container">
	<!-- FORM SECTION -->
	<div class="row">
		<!-- SIGN UP -->
		<div class="col align-items-center flex-col sign-up">
			<div class="form-wrapper align-items-center">
				<div class="form sign-up"></div>
			</div>

		</div>
		<!-- END SIGN UP -->
		<!-- SIGN IN -->
		<div class="col align-items-center flex-col sign-in">
			<div class="form-wrapper align-items-center">
<!-- 				<form method="post" action="j_security_check" id="login"> -->
					<div class="form sign-in">
						<div class="input-group">
							<i class='bx bxs-user'></i>
							<input type="text" placeholder="Username" id="j_username">
						</div>
						<div class="input-group">
							<i class='bx bxs-lock-alt'></i>
							<input type="password" placeholder="Password" id="j_password">
						</div>
						<input type="checkbox" name="checkId" id="checkId" value="checkbox" style="margin-bottom:20px;">
						&nbsp;ID저장
						<input type="button" id="login" value="Sign In" onclick="javascript:enter();">
					</div>
<!-- 				</form> -->
			</div>
			<div class="form-wrapper"></div>
		</div>
		<!-- END SIGN IN -->
	</div>
	<!-- END FORM SECTION -->
	<!-- CONTENT SECTION -->
	<div class="row content-row">
		<!-- SIGN IN CONTENT -->
		<div class="col align-items-center flex-col">
			<div class="text sign-in">
				<h2>Welcome</h2>

			</div>
			<div class="img sign-in"></div>
		</div>
		<!-- END SIGN IN CONTENT -->
	</div>
	<!-- END CONTENT SECTION -->
</div>
<script type="text/javascript">

  let container = document.getElementById('container')

	toggle = () => {
	  container.classList.toggle('sign-in')
	}

	setTimeout(() => {
	  container.classList.add('sign-in')
	}, 200)
	
</script>
</body>
</html>