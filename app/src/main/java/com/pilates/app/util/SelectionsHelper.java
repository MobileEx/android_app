package com.pilates.app.util;

import com.pilates.app.model.dto.SelectionItemDto;
import com.pilates.app.model.dto.SelectionListDto;

import java.util.ArrayList;
import java.util.List;

public class SelectionsHelper {
    public static ArrayList<String> getIds(List<SelectionItemDto> source) {
        ArrayList<String> ids = new ArrayList<>();
        for(SelectionItemDto item : source) {
            ids.add(item.getId());
        }

        return ids;
    }

    public static SelectionItemDto findById(String id, SelectionListDto source) {
        for(SelectionItemDto item : source.getData()) {
            if(item.getId().equals(id))
                return item;
        }

        return null;
    }

    public static SelectionItemDto findById(String id, List<SelectionItemDto> source) {
        for(SelectionItemDto item : source) {
            if(item.getId().equals(id))
                return item;
        }

        return null;
    }

    public static ArrayList<SelectionListDto> getMultiRowSelection(SelectionListDto source) {
        ArrayList<SelectionListDto> rows = new ArrayList<>();
        SelectionListDto row = new SelectionListDto();
        row.setData(new ArrayList<>());

        rows.add(row);

        int pIdx = 1;
        for(SelectionItemDto item : source.getData()) {
            if(pIdx > 3) {
                SelectionListDto newRow = new SelectionListDto();
                newRow.setData(new ArrayList<>());

                rows.add(newRow);
                pIdx = 0;
            }

            rows.get(rows.size() - 1).getData().add(item);

            pIdx++;
        }

        return rows;
    }
}
