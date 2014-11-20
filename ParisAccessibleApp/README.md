Bored 
=========

Bored web application

Technologies
----
  - Java 7 J2EE
  - Spring MVC 4.x + Spring Boot
  - Jest elasticsearch java http 
  - Maven 3

Conf
----
JVM Env:
  - BONSAI_URL : elasticsearch 
 
Attributes
----
- OpeningDay : Lundi,Mardi,Mercredi,Jeudi,Vendredi,Samedi,Dimanche
- OpeningDayTime : Matin, Midi, Apresmidi, Soiree, Nuit
- SocialStatus : Seul,Couple,Groupe
- Weather :     Soleil, Nuageux, Pluie
-

Data injection
----
Web UI @ {host}/inject/
upload csv file with structure similar to 
	[this file](https://github.com/ajacquem16/Bored/blob/master/injection_structure/inject.csv) 

ActivityAPI
----
```java
// retreive activites

@RequestMapping(value = "/activity", method = RequestMethod.GET)
@RequestParam("latitude") Double latitude,
@RequestParam("longitude") Double longitude,
@RequestParam(value = "user_id", required = false) String user_id,
@RequestParam(value = "distance", required = false) String distance,
@RequestParam(value = "socialStatus", required = false) List<SocialStatus> socialStatus,
@RequestParam(value = "weather", required = false) List<Weather> weather,
@RequestParam(value = "openingDays", required = false) List<OpeningDay> openingDays,
@RequestParam(value = "openingDayTime", required = false) List<OpeningDayTime> openingDayTime

exemples:
{host}/activity/?latitude= 48.866042&longitude=2.343582&username=sdfsdfd&openingDayTime=Nuit
{host}/activity/?latitude= 48.866042&longitude=2.343582&username=sdfsdfd&openingDayTime=Nuit&fields=name&fields=bigDescription
{host}/activity/?latitude= 48.866042&longitude=2.343582&username=sdfsdfd&socialStatus=Groupe&socialStatus=Seul&openingDayTime=Nuit&openingDayTime=Soiree
```
UserAPI (not disponible now)
----
```java
// create/update user
@RequestMapping(value = "/user", method = RequestMethod.POST)
@RequestParam("id") String id,
@RequestParam(value = "like", required = false) List<String> like,
@RequestParam(value = "dislike", required = false) List<String> dislike) 
```
```java
// add like/dislike
@RequestMapping(value = "/user/category/add", method = RequestMethod.POST)
@RequestParam("id") String id,
@RequestParam(value = "like", required = false) List<String> like,
@RequestParam(value = "dislike", required = false) List<String> dislike
```
```java
// remove like/dislike
@RequestMapping(value = "/user/category/remove", method = RequestMethod.POST)
@RequestParam("id") String id,
@RequestParam(value = "like", required = false) List<String> like,
@RequestParam(value = "dislike", required = false) List<String> dislike
```