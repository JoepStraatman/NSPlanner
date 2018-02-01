Description: <br/>
Dit is een routeplanner voor de treinen in nederland, om zelf routes samen te stellen aan de hand van het handmatig uitkiezen van overstappen.
<img src="https://github.com/JoepStraatman/NSPlanner/blob/master/doc/Screenshot_20180201-165230.png?raw=true" width="250">

Technical design: <br/>
In de NSPlanner heb ik 3 verschillende API's gebruikt. Om de stations te vinden heb ik de NS API stationslijst gebruikt. Om routes te zoeken heb ik de NS API Reisadvies gebruikt. En op de gegevens op te slaan gebruik ik Firebase. Hiermee kan de user inloggen en kan hij/zij routes opslaan en verwijderen. Ook kan de user recente routes zien die gepland zijn door hem of haar.

Om de app te realiseren heb ik 3 verschillende soorten java files gebruikt:<br/>
Activity, Adapter, Data. <br/>

Om te beginnen kom je in de HomeActivity, dit is de startpagina van de app. Hier kan je doorgaan naar het favorieten scherm, naar een nieuwe reis maken gaan, of op een recente reis drukken waardoor je die gelijk te zien krijgt.
Deze Activity gebruikt de HomeListAdapter om de recente reizen te laten zien. Als de user nog niet is ingelogd wordt deze automatisch doorverzonden naar de LoginActivity, die vervolgens als er is ingelogd terug komt in de HomeActivity. Overigens kan je in elke activity rechts bovenin uitloggen.

Als je in de HomeActivity op Nieuw klikt kom je in de ReisActivity. Hier kan je de details van je reis invullen. Je kan de reis een naam geven, je kan een vertrekstation kiezen en een aankomststation. (de vertrekstations worden uit de NS API stationlijst gehaald) Ook kan je een tijd en datum opgeven voor de route(standaard huidige tijd/datum). Als laatste kan je kiezen of de tijd die je invult de vertrek of aankomst tijd is door op de knop te drukken.

Als je dan vervolgens op Plan je reis klikt kom je in de TijdActivity. In deze activity wordt de routedate opgehaald uit de NS API reisinformatie. In een listview worden alle tijden rondom de aangegeven tijd weergegeven, met vertragingen mits die er zijn er bij. Ook wordt er een rode driehoek weergegeven als een reis helemaal niet mogelijk is. Je kan hier door de tijden scrollen en een tijd kiezen. De TijdActivity gebruikt de TijdListAdapter om de data te laten zien.

Als er op een tijd geklikt wordt kom je in de RouteActivity. Ook deze heeft een listview, waar de RouteListAdapter voro gebruikt wordt, waarin de stations met tussenstops staan. Alle tijden wordt weergegeven met vertraging en als een route niet mogelijk is staat dat bovenaan duidelijk in een rode balk, met de rede erbij.

Als je dan op Toevoegen drukt wordt de route in Firebase in onlangs opgeslagen. Waardoor deze route altijd terug te vinden is op het homescherm onder recente routes. De activiteit waar je dan in komt is de RoutePlanActivity. Deze gebruikt de RoutePlanAdapter om de data  weer in een listview te laten zien. Echter om de data uit Firebase op te halen gebruikt deze activiteit ook nog de RouteData class. In dit scherm zie je de naam van de reis, die eerder gekozen was. Ook kan je de reis aan het favorietenscherm toevoegen door rechts bovenin op de checkbox te drukken. Als op overstap toevoegen wordt gedrukt komt de gebruiker weer in de ReisActivity. Echter is een nu een parameter meegegeven (overstap) waardoor er wordt onthouden dat dit een overstap is en niet een nieuwe reis en deze aan de totale reis wordt toegevoegd.

Challenges: <br/>
Ik ben lang bezig geweest met het inloggen op de API vanuit android. Dit wilde niet lukken en was ook bijna nergens te vinden. Vervolgens was het data doorgeven ook behoorlijk lastig. Ik ben niet helemaal tevreden hoe ik het nu heb gedaan, maar dit was de simpelste oplossing. Ook met Firebase was het soms nog erg vervelend. Die wilde blijven listenen, maar hiervoor moest een singleEvent gebruikt worden om dit op te lossen. Verder wil de API soms de verkeerde volgorde doen van requests waardoor de overstappen in de verkeerde volgorde komen. Dit heeft te maken met de snelheid van het internet waardoor die de een soms ineens sneller pakte dan de ander. 

Ik ben er achter gekomen dat het verwerken van al deze data per scherm best een lastige klus was en dat het iets moeilijker uit pakte dan ik had verwacht. Hierdoor heb ik het Google maps deel moeten schrappen, wat ik opzich jammer vind, maar wat verder niet heel veel uitmaakt voor het nut van mijn app op dit moment. Ook was ik in het design document een inlogscherm vergeten en had ik een extra activiteit nodig om de opgeslagen routes te laten zien. Ook heb ik hier de reisActiviteit herbruikt terwijl ik in 1e instantie hier een apart scherm voor wilde creeeren.

Argument: <br/>
Het herbruiken van de reisactiviteit leek mij ruimte te besparen op de telefoon, het is zonde om die nog een keer te maken als dit niet nodig is. Mijn huidige oplossing voor data doorgeven was gemakkelijk op dit moment. Allemaal arrays als parameters doorgeven, wat niet echt mooi en kort is maar wat wel op dit moment werkte om het af te krijgen. Als ik meer tijd had gehad, zou ik een betere manier willen hebben gevonden om dit probleem op te lossen door bijvoorbeeld een hulpklasse te definieeren.

Final state:<br/>
Ik mag best tevreden zijn over de final state van mijn app. Grotendeels werkt alles zoals ik het had gewild en heb wat dingen hier en daar moeten schrappen, maar heb ook weer dingen toegevoegd die ik in eerste in stantie niet belangrijk vond. Overal werkt de app wel goed, en nog redelijk snel ook.
