 public static BigDecimal getBigDecimal(Row row, int index,int scale) {


        try {

            if (row.getCell(index).getCellType() == CellType.STRING) {
                var value =row.getCell(index).getStringCellValue();
                if(Objects.equals(value, "-"))
                {
                    value="0.0000";
                }
                return BigDecimal.valueOf(Double.valueOf(value)).setScale(scale, RoundingMode.HALF_UP);
            } else if (row.getCell(index).getCellType() == CellType.NUMERIC) {
                Double valDouble = row.getCell(index).getNumericCellValue();
                return BigDecimal.valueOf(valDouble).setScale(scale, RoundingMode.HALF_UP);
            }


        } catch (Exception e) {


        }
        return null;

    }
