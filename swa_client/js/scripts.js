$('#info1').hide();
$('#info2').hide();


var myURL = 'http://localhost:8080/swa/rest/aziende';
var uri = [[],[]];

//richiesta al server
$.getJSON( myURL, function(data) {
    console.log(data);

    var items = [];
    $.each(data, function(key, value) {

      //lista collegamenti url alle aziende
      uri[0].push(value.nome);
      uri[1].push(value.url);

      //lista di selezione in HTML
      items.push("<option value='" + key + "'>" + value.nome + "</option>");
    });

    $('#selezione').append(items);
});
console.log(uri);

$('#selezione').change(function() {

  var id = $(this).val();
  console.log(uri[id]);

  if(id === 'def'){
    $('#info1').hide();
  }else{

    //richiesta al server delle informazioni sull'azienda selezionata
    $.getJSON(uri[1][id], function(data){
        console.log(data);

        //info
        $('#iva').html("P.IVA: " + data.partitaIva);
        $('#ragsoc').html("Ragione Sociale: "+ data.ragioneSociale);
        $('#provincia').html("Provincia: " + data.provincia);
        $('#disc').html("Data Iscrizione: " + data.dataIscrizione);
        $('#dter').html("Data Termine: " + data.dataTermine);
        $('#foro').html("Foro Competente: " + data.foroCompetente);
        $('#rappresentante').html("Responsabile: " + data.cognomeRappresentante+" "+data.nomeRappresentante);
        $('#responsabile').html("Rappresentante: " + data.cognomeResponsabile+" "+data.nomeResponsabile);

        //contatti
        $('#telR').html("Tel Responsabile: " + data.telResponsabile);
        $('#email').html("Email: " + data.email);
        $('#emailR').html("Email Responsabile: " + data.emailResponsabile);
        $('#sede').html("Sede: " + data.indirizzoSede);
        $('#citta').html("Città: " + data.citta);
        $('#cap').html("Cap: " + data.cap);
    });

    //nome dell'azienda
    $('#nomeAzienda').html(uri[0][id]);
    $('#info1').show();
  }






});
