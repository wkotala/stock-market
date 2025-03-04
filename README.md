# stock-market

This project was done for a college class, where I've learnt OOP and JUnit framework.

# System Transakcyjny Giełdy Papierów Wartościowych

## Opis
System obsługuje transakcje kupna i sprzedaży akcji spółek. Zlecenia zawierają:
- Typ zlecenia: kupno/sprzedaż
- Identyfikator akcji (ciąg znaków ASCII A-Z, maks. 5 znaków)
- Liczba akcji (dodatnia liczba całkowita)
- Limit ceny (dodatnia liczba całkowita)

Transakcje realizowane są w przypadku zgodności cen zleceń kupna i sprzedaży (tj. gdy cena kupna nie jest niższa od ceny sprzedaży).

## Rodzaje Zleceń
1. **Natychmiastowe** – musi być zrealizowane (choćby częściowo) w tej samej turze; pozostała część jest eliminowana.
2. **Bez określonego terminu** – czeka na pełną realizację.
3. **Wykonaj lub anuluj** – musi być w całości zrealizowane w tej samej turze, inaczej jest eliminowane.
4. **Do końca określonej tury** – ważne do końca wskazanej tury lub do realizacji.

## Tury i Kolejność Zleceń
- Realizacja zleceń przebiega w turach.
- Priorytet mają: cena, tura zgłoszenia, kolejność w turze.
- Transakcje zawierane są po cenie starszego zlecenia.

## Typy Inwestorów
1. **RANDOM** – podejmuje losowe decyzje o zleceniach (kupno/sprzedaż, akcje, liczba, limit ceny).
2. **SMA** – bazuje na analizie wskaźnika Simple Moving Average (SMA 5 i SMA 10). Kupno następuje, gdy SMA 5 przecina od dołu SMA 10, sprzedaż – gdy SMA 5 przebija od góry SMA 10.

## Założenia Systemu
- Akcje są dodatnimi liczbami naturalnymi.
- Inwestorzy muszą posiadać aktywa przy składaniu zleceń (zakup wymaga gotówki, sprzedaż – posiadania akcji).
- Krótkie sprzedaże są niedozwolone.
- System pozwala na łatwe dodawanie nowych typów inwestorów.

## Wywołanie Programu
Przykład:  
```shell
java GPWSimulation input.txt 100000
```

gdzie input.txt to plik wejściowy określający stan początkowy, a 100000 to liczba tur symulacji.

Przykładowe pliki wejściowe można znaleźć w folderze input/
