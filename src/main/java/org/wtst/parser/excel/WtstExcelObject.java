package org.wtst.parser.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.wtst.parser.WtstObject;
import org.wtst.parser.excel.object.WtstCellObject;
import org.wtst.parser.excel.object.WtstFontObject;
import org.wtst.parser.excel.object.WtstSheetObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WtstExcelObject implements WtstObject {

    private final Map<String, ?> entries;

    private final Workbook workbook;

    private final Sheet sheet;

    private final Row row;

    public WtstExcelObject(Map<String, ?> entries, Workbook workbook, Sheet sheet) {
        this(entries, workbook, sheet, null);
    }

    public WtstExcelObject(Map<String, ?> entries, Workbook workbook, Sheet sheet, Row row) {
        this.entries = entries;

        this.workbook = workbook;
        this.sheet = sheet;
        this.row = row;
    }

    public Map<String, ?> getEntries() {
        return entries;
    }

    public WtstSheetObject sheet() {
        return new WtstSheetObject(workbook, sheet);
    }

    public WtstCellObject cell(int index) {
        return new WtstCellObject(workbook, sheet, row, row == null ? null : row.getCell(index));
    }

    public WtstFontObject font(int index) {
        return cell(index).font();
    }

    public Object value(int index) {
        return cell(index).value();
    }

    public Stream<Object> stream(int... indexes) {
        return Arrays.stream(indexes).mapToObj(this::value);
    }

    public Object[] array(int... indexes) {
        return stream(indexes).toArray();
    }

    public List<Object> list(int... indexes) {
        return stream(indexes).collect(Collectors.toCollection(LinkedList::new));
    }

    public Set<Object> set(int... indexes) {
        return stream(indexes).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public int hash(int... indexes) {
        return Arrays.hashCode(array(indexes));
    }
}
