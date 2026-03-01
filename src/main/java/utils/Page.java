package utils;

import java.util.List;

public class Page<E> {

    private List<E> elementsOnPage;
    private int numberOfElementsPage;
    private int totalNumberOfElements;

    public Page(List<E> elementsOnPage, int numberOfElementsPage,int totalNumberOfElements) {
        this.elementsOnPage = elementsOnPage;
        this.numberOfElementsPage = numberOfElementsPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    public List<E> getElementsOnPage() {
        return elementsOnPage;
    }

    public int getNumberOfElementsPage() {
        return numberOfElementsPage;
    }

    public int getTotalNumberOfElements() {return totalNumberOfElements;}
}
