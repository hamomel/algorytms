# Приложение "мини поисковик по базе документов" #

## Функционал приложения: ##
    Из командной строки запускается приложение с параметром указывающим на корневую папку
    в котророй хранится библиотека.
    Если папка уже проиндексирована и не изменялась с момента последнего запуска программы
    то пользователю предлагается ввести фразу для поиска.
    Фраза должна содержать не менее одного слова длинной не менее 4 символов.
    После ввода фразы пользователю отображается список документов в которых эта фраза может
    присутствовать. Список сортирован по степени вероятности вхождения фразы.
    После вывода результата, пользователю предлагается ввести новый запрос.
    Для выхода нужно набрать "exit"
    
## Реализация: ##
    Служебные файлы создаваемые программой:
        1. files.csv - первая строка содержит путь к индексируемой папке, каждая последующая
           путь к файлу документа и время последнего изменения.
        2. index.csv - в каждой строке содержится:
           хэш_слова,имя_файла_содержащего_слово,разделенные_запятыми_индексы_вхождений_слова_в_документ
        
    Структуры данных в программе:
        1. Массив файлов найденных в индексируемой директории, сортированный в порядке обхода директроии
        2. Массив размером 2^16, каждая чейка которого содержит массив массивов в каждом из корорых: 
           в первой ячейке индекс файла в массиве файлов, в последующих ячейках индексы вхождений слова в файле
           
    Алгоритм работы программы:
        Если files.csv не существует или первая строка указывает на папку отличную от введенной пользователем 
        - запускается полная индексация.
        Иначе: 
            - обходим папку рекурсивно 
                Для каждого найденного файла: 
                - проверяем есть ли он в files.csv и не изменилось ли время его последнего изменения.
                   Если файл или время его изменения не совпадают с записанными в files.csv 
                   - запускается полная индексация.
            Если количество найденных файлов не соответствует количеству в files.csv:
            - запускается полная индексация.
            Иначе: 
            - восстанавливается индекс из файла index.csv 
        - пользователю предлагается ввести фразу для поиска. 
        
    Алгоритм полной индексации:
        - создать пустой массив размером 2^16 хранимый тип - ссылка 
        - обоходим массив с найденными в папке файлами 
            Для каждого файла: 
            - создать переменную "позиция" инициализировать 0 
            - читаем строки последовательно
                Для каждой строки: 
                - заменить все знаки кроме букв и цифр пробелами 
                - разделить строку на слова по пробелам.
                    Для каждого полученного слова:
                    - удалить пробелы в начале и в конце 
                    - создать хэш слова
                    - найти в массиве индексов позицию соответствующую хэшу 
                    - проверить есть ли там массив вхождений 
                        Есть:
                        - проверить есть ли в массиве запись для индексируемого файла
                            Есть:
                            - добавить в массив значение переменной "позиция" методом сортировки вставкой
                            Нет:
                            - добавить новый массив
                            - в первую ячейку записать индекс файла в массиве файлов
                            - во вторую записать значение переменной "позиция"
                        Нет:
                        - добавить новый массив в ячейку с адресом хэша слова
                        - добавить новый массив в массив созданный на предыдущем шаге
                        - в первую ячейку записать индекс файла в массиве файлов
                        - во вторую записать значение переменной "позиция"
                    - добавить к "позиция" длину слова до удаления пробелов + 1
        - запустить в отдельном потоке сохранение полученного индекса на диск
        
    Алгоритм сохранения на диск:
        - создать файл files.csv
        - записать в превоую строку имя папки введенной пользователем
        - обходим массив в файлами
            Для каждого файла:
            - записать "имя,время изменения"
        - создать файл index.csv
        - объходим массив в индексами
            Для каждой записи в массиве:
                Ячейка не пуста:
                - обходим массив в ячейке
                    Для каждой ячейки:
                    - записать в index.csv строку "номер ячейки в массиве индексов,разделенные_запятыми_значения_в_массиве"
                Ячейка пуста:
                - перейти к следующей
                
     Алгоритм восстановления индекса с диска:
         - создать массив размером 2^16
         Пока есть строки в index.csv
             - создать массив типа String
             - читать следующую строку
             - записать в массив
                 Пока первое значение следующей строки равно первому значению в строке в листе
                 - добавить следующую строку в массив
             - создать массив длинной равной длине масива строк
             - записать в каждую ячейку массива массив со значениями соответствующей строки начиная со второго
             - записать созданный массив в ячейку с номером из первого значенния строки в массив индексов
       
      Алгоритм поиска:
          - заменить в поисковой строке все знаки кроме букв и цифр пробелами
          - разбить строку на слова по пробелам
          - создать массив типа ссылка на массив
          Для каждого слова:
              - удалить пробелы в начале и в конце
              - найти хэш
              - найти массив вхождений слова в массиве индексов
              - записать найденный массив в массив созданный в начале
          - удалить из полученного массива элементы, первое значение которых не равно первому значению 
            одного из массивов в остальных ячейках
          Если массив не пуст:
              Для каждого элемента в массиве вхождений первого слова:
                  Пока есть массив для следующего слова в этом же документе:
                      - найти в массиве вхождений следующего слова индекс который больше индекса текущего 
                        на длину текущего слова + 1 и меньше индекса первого на длину текущего слова + 10
                          Если индекс найден:
                              - перейти к массиву для следующего слова
                              - начать обход с найденного индекса
                          Иначе:
                              - перейти к первому массиву вхождений первого слова в следующем документе
                      Если найден индекс в массиве последнего слова:
                          - записать найденные индексы для документа в результирующий массив
              Если результирующий массив не пуст:
                  - отсортировать массив по сумме разниц между индексами всех слов для каждого документа
                  - покажать пользователю список в порядке: меньшая сумма разниц - выше позиция
              Иначе:
                  - вывести сообщение "ничего не найдено"
          Иначе:
              - вывести сообщение "ничего не найдено"
      