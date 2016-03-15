<%@ page language="java" pageEncoding="UTF-8"%>

<!-- page content -->
<div id="content_frame">
<h1>Create an Account </h1>
<p>
Your Google Account gives you access to OlapTagCloud. 
If you already have a Google Account, 
<!--  http://localhost:8080/OlapTagCloud/ -->
you can <a href="?page=login">sign in here</a>. 
</p>
<form id="account" method="get">
	<fieldset><legend>Get started with OlapTagCloud</legend>
	<div class="lineformright"><input type="text" id="username" /></div>
	<div class="lineform">Username</div>
	<div class="lineformright"><input type="text" id="email" /></div>
	<div class="lineform">E-mail</div>
	<div class="lineformright"><input type="password" id="cpass" /></div>
	<div class="lineform">Choose a password</div>
	<div class="lineformright"><input type="password" id="rpass" /></div>
	<div class="lineform">Re-enter password</div>
	<div class="lineformright"><input type="submit" id="create"  value="Create my account" /></div>
	<div class="lineform">&nbsp;</div>
	</fieldset>
</form>	
</div>