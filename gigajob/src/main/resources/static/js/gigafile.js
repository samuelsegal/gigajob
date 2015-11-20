var todo_global_js_ok_or_no;
function onFileSelected(event) {
	
	var selectedFile = event.target.files[0];
	var reader = new FileReader();
	var imgtag = document.getElementById(todo_global_js_ok_or_no);
	imgtag.title = selectedFile.name;
	imgtag.src = selectedFile.name;
	reader.onload = function(event) {
	  imgtag.src = event.target.result;
	};
	reader.readAsDataURL(selectedFile);				
}

function chooseFile(imgNum) {
	
	todo_global_js_ok_or_no = 'postNail' + imgNum;
	$("#fileInput" +imgNum).click();
}	