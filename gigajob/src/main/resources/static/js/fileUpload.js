console.log('yooo');

// Variable to store your files
var gigafiles;

// Add events
$('#fileInput').on('change', prepareUpload);

// Grab the files and set them to our variable
function prepareUpload(event)
{
	console.log('preparing your face 4 load');
	gigafiles = event.target.files;
	console.log('files: ');
	console.dir(gigafiles);
}

$('form').on('submit', uploadFiles);

// Catch the form submit and upload the files
function uploadFiles(event)
{
	event.stopPropagation(); // Stop stuff happening
    event.preventDefault(); // Totally stop stuff happening

    // START A LOADING SPINNER HERE

    // Create a formdata object and add the files
    var data = new FormData();
    $.each(gigafiles, function(key, value)
    {
        data.append(key, value);
    });
    
    $.ajax({
        url: '/jobyjob/upload',
        type: 'POST',
        data: data,
        cache: false,
        dataType: 'json',
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        success: function(data, textStatus, jqXHR)
        {
            if(typeof data.error === 'undefined')
            {
                // Success so call function to process the form
                submitForm(event, data);
            }
            else
            {
                // Handle errors here
                console.log('ERRORS: ' + data.error);
            }
        },
        error: function(jqXHR, textStatus, errorThrown)
        {
            // Handle errors here
            console.log('ERRORS: ' + textStatus);
            console.log(errorThrown);
            console.dir(jqXHR);
            // STOP LOADING SPINNER
        }
    });
}