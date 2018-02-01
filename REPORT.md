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

Als je dan vervolgens op Plan je reis klikt kom je in de TijdActivity. In deze activity wordt de data opgehaald uit de API
