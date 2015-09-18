# ITIS-ANDROID-PRACTICE
Летняя практика - лаборатория "Android"

####Преподаватель: Ильсеяр Алимова,????

##### Задания:
###### Задание№1 
Нужно получить с сервера 10 ближайших к пользователю геолокационных объектов. После этого нужно построить маршрут на карте по всем объектам с началом в точке текущего местоположения пользователя
<ul>
<li>Геолокационные объекты получаются с помощью геокодера Яндекс Карт</li>
<li>Работа с картой выполняется через Google Maps Api для Android</li>
<li>Приложение должно поддерживать смену ориентаций.</li>
<li>Порядок городов при построении маршрута произвольный</li>
<li>Дополнительно:
  <ul>
      <li>можно реализовать построение кратчайшего маршрута</li>
  </ul>
</li>
</ul>

</ul>
######Исходники: [sources](https://github.com/dalv666/ITIS-ANDROID-PRACTICE/tree/master/app/src/main/java/com/googlemaps/template/myapplication)
###### Задание№2

Cоздать приложение “Галерея”, где пользователь может просматривать набор картинок, которые скачиваются с сервера.

<ul>
<li>Количество столбцов в галерее должно определяться устройством и текущей ориентацией. Для телефона в портретной ориентации должен быть 1 столбец, в альбомной - 2. Для планшета в портетной ориентации должно быть 2 столбца, в альбомной - 3</li>
<li>Приложение должно отображать процесс загрузки изображений в виде placeholder для каждого изображения.</li>
<li>Изображения должны корректно изменять свой размер в зависимости от количества столбцов и размера экрана. </li>
<li>При скролле изображений должен пропадать тулбар (Collapsing toolbar).</li>
<li>При нажатии на изображение переход на другой экран должен осуществляться с помощью Shared Element Transition (только для Android 5+).</li>
<li>Дополнительно:
  <ul>
      <li>Реализовать кэширование картинок с возможностью отображения их при отсутствии интернета.</li>
      <li>Можно реализовать Transition переход для < Android 5, сохранив при этом работу со стандартным Shared Element Transition для Android 5+.</li>
  </ul>
</li>
</ul>
######Исходники: [sources](https://github.com/dalv666/ITIS-ANDROID-PRACTICE/tree/ImageGallery/app/src/main/java/org/dalv/practice/android/itis/customgallery)
