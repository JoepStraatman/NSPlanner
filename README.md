# NSPlanner
final project

Het probleem:

Op dit moment zijn er 2 grote apps waar je reisinformatie kan opvragen. De NS app en 9292. Op beide sites kan je dus een route vinden om van A naar B te komen. Echter soms wil je een specifieke route afgaan. Nou heb je wel de mogelijkheid om te kiezen dat je via een station gaat, maar kan je niet precies de route zelf uitstippelen die je wilt. 
Ook is er soms het probleem dat er staat dat een overstap niet mogelijk is of niet bestaat terwijl dit wel het geval is. Als je al een lange reis moet maken net als ik, dan wil je dat de route zo kort mogelijk is en dus niet onnodig staat de wachten.

Oplossing:

Een app waarin je zelf je route kan uitstippelen en niet gebonden bent aan de standaaroverstap tijd die nodig is, en waarin je je favoriete routes kan opslaan.

Main features:
Een reis plannen van A naar B. 
Zelf verschillende overstappen toevoegen. 
Ook via maps een looproute zien naar het station. (op de kaart zien sowieso, de route optioneel)
Favoriete opslaan

Optioneel:
keuzes uit verschillende voorgestelde dingen


Data sources:
NS API
Maps API

verder niks bijzonders, de data uit ns valt eenvoudig uit te lezen.
Ook maps is makkelijk.
Misschien een lijst van stations en coordinaten nodig, ligt aan ns api. moet ik nog uitzoeken.

External components:
sqlite of firebase voor een database die de favorieten opslaat.

similar:
Ns app en 9292 app.
die doen bijna hetzelfde maar kunnen alleen vooraf geselecteerde routes tonen. Bij ns kan je favorieten toevoegen bij 9292 niet. Ook hebben ze de maps route.

Hardest:
met maps werken (nooit gedaan)
voorgestelde opties voor stations geven.
