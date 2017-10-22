Desciption : 
Météo-France est le service officiel et public de la météorologie et du climat en France. Dans
le mouvement récent des données ouvertes (open data), Météo-France met à la disposition
de tous les utilisateurs les données produites dans le cadre de ses missions de service public.
On trouve par exemple des données climatiques mesurées toutes les 3 heures sur
différentes stations d’observation en France (Brest-Guipavas, Orly, Tour …) :
https://donneespubliques.meteofrance.fr/?fond=produit&id_produit=90&id_rubrique=32
Nous proposons ici de mettre en place un outil pour exploiter ces données climatiques, et
ainsi offrir à l’utilisateur les moyens d’accéder à ces données, de les visualiser et de les
manipuler.
On propose donc de faire une application « pour le bureau » permettant de visualiser
l’évolution du climat sur une durée donnée (un an, un mois, un jour), pour une station
donnée, à la fois. On veut ainsi pouvoir afficher l’évolution des températures en degrés
Celsius ou Kelvin, de l’humidité (en %) et de la nébulosité totale (couverture nuageuse en %)
sur la durée.
L’application doit également permettre à l’utilisateur de faire des comparaisons de ces
évolutions (affichage de courbes, différence et écart-type moyens) :
 - Deux années au choix ;
 - Un mois au choix avec le même mois d’une autre année ;
 - Un jour au choix avec le même jour d’une autre année.

Fonctionnement : 
L’application récupère les données à partir du site France météo sous forme de fichier CSV enregistrées dans un dépôt local qui a pour chemin : C:\smartClimateDepot.
L’application vous permettra aussi de vider le cache (supprimer le dossier smartClimateDepot  créé auparavant). 
