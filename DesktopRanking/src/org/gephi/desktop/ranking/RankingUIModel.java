/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.desktop.ranking;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingEvent;
import org.gephi.ranking.api.RankingListener;
import org.gephi.ranking.api.RankingModel;
import org.gephi.ranking.api.Transformer;

/**
 *
 * @author Mathieu Bastian
 */
public class RankingUIModel implements RankingListener {
    //Const

    public static final String CURRENT_TRANSFORMER = "currentTransformer";
    public static final String CURRENT_RANKING = "currentRanking";
    public static final String LIST_VISIBLE = "listVisible";
    public static final String BARCHART_VISIBLE = "barChartVisible";
    public static final String CURRENT_ELEMENT_TYPE = "currentElementType";
    public static final String RANKINGS = "rankings";
    //Current model
    private String currentElementType;
    private final Map<String, Ranking> currentRanking;
    private final Map<String, Transformer> currentTransformer;
    protected boolean barChartVisible;
    protected boolean listVisible;
    //Architecture
    private final List<PropertyChangeListener> listeners;
    private RankingUIController controller;
    private final RankingModel model;

    public RankingUIModel(RankingUIController rankingUIController, RankingModel rankingModel) {
        listeners = new ArrayList<PropertyChangeListener>();
        currentRanking = new HashMap<String, Ranking>();
        currentTransformer = new HashMap<String, Transformer>();
        model = rankingModel;
        controller = rankingUIController;
        currentElementType = Ranking.NODE_ELEMENT;

        //Set default transformer - the first
        for (String elementType : controller.getElementTypes()) {
            currentTransformer.put(elementType, controller.getTransformers(elementType)[0]);
        }
        
        model.addRankingListener(this);
    }

    public void rankingChanged(RankingEvent event) {
        firePropertyChangeEvent(RANKINGS, null, null);
    }

    public void setCurrentTransformer(Transformer transformer) {
        if (currentTransformer.get(currentElementType) == transformer) {
            return;
        }
        Transformer oldValue = currentTransformer.get(currentElementType);
        currentTransformer.put(currentElementType, transformer);
        firePropertyChangeEvent(CURRENT_TRANSFORMER, oldValue, transformer);
    }

    public void setCurrentRanking(Ranking ranking) {
        if ((currentRanking.get(currentElementType) == null && ranking == null)
                || (currentRanking.get(currentElementType) != null && currentRanking.get(currentElementType) == ranking)) {
            return;
        }
        Ranking oldValue = currentRanking.get(currentElementType);
        currentRanking.put(currentElementType, ranking);
        firePropertyChangeEvent(CURRENT_RANKING, oldValue, ranking);
    }

    public void setListVisible(boolean listVisible) {
        if (this.listVisible == listVisible) {
            return;
        }
        boolean oldValue = this.listVisible;
        this.listVisible = listVisible;
        firePropertyChangeEvent(LIST_VISIBLE, oldValue, listVisible);
    }

    public void setBarChartVisible(boolean barChartVisible) {
        if (this.barChartVisible == barChartVisible) {
            return;
        }
        boolean oldValue = this.barChartVisible;
        this.barChartVisible = barChartVisible;
        firePropertyChangeEvent(BARCHART_VISIBLE, oldValue, barChartVisible);
    }

    public void setCurrentElementType(String elementType) {
        if (this.currentElementType.equals(elementType)) {
            return;
        }
        String oldValue = this.currentElementType;
        this.currentElementType = elementType;
        firePropertyChangeEvent(CURRENT_ELEMENT_TYPE, oldValue, elementType);
    }

    public Ranking getCurrentRanking() {
        return currentRanking.get(currentElementType);
    }

    public Transformer getCurrentTransformer() {
        return currentTransformer.get(currentElementType);
    }

    public Transformer getCurrentTransformer(String elementType) {
        return currentTransformer.get(elementType);
    }

    public String getCurrentElementType() {
        return currentElementType;
    }

    public boolean isBarChartVisible() {
        return barChartVisible;
    }

    public boolean isListVisible() {
        return listVisible;
    }

    public Ranking[] getRankings() {
        Ranking[] rankings = model.getRankings(currentElementType);
        Ranking current = getCurrentRanking();
        if (current != null) {
            //Update selectedRanking with latest version
            for (Ranking r : rankings) {
                if (r.getName().equals(current.getName()) && r.getClass().equals(current.getClass())) {
                    currentRanking.put(currentElementType, r);
                    break;
                }
            }
        }
        return rankings;
    }

    public Transformer[] getTransformers() {
        return controller.getTransformers(currentElementType);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    private void firePropertyChangeEvent(String propertyName, Object beforeValue, Object afterValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, beforeValue, afterValue);
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }
}