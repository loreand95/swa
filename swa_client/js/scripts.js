$('#info1').hide();
$('#info2').hide();

//Richiesta al server
var myURL = 'http://localhost:8080/swa/rest/aziende';
var urls = [];
$.getJSON( myURL, function(data) {
    console.log(data);

    var items = [];
    $.each(data, function(key, value) {

      //lista collegamenti url alle aziende
      urls.push(value.url);

      //lista di selezione in HTML
      items.push("<option value='" + key + "'>" + value.nome + "</option>");
    });

    console.log(items);
    $('#selezione').append(items);
});
console.log(urls);

$('#selezione').change(
  function() {
  forEach(i: urls) {
    if ($(this).val() === i) {
      $('#info1').show();
      $('#info2').show();
    };
  };
}
);
