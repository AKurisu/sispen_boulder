package untad.aldochristopherleo.sispenboulder.util

import android.util.Log
import io.github.evanrupert.excelkt.Sheet
import io.github.evanrupert.excelkt.Workbook
import io.github.evanrupert.excelkt.workbook
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.util.CellRangeAddress
import untad.aldochristopherleo.sispenboulder.data.SortedResult

class PrintData(val result: ArrayList<SortedResult>) {
    private lateinit var dataToPrint : Workbook

    init {
        init()
    }

    private fun init(){


        dataToPrint =
        workbook {
            sheet {
                docHeader()
                val nameWrap = createCellStyle{
                    wrapText = true
                }
                row {  }
                var i = 1
                for(item in result){
                    row {
                        cell(i++, nameWrap)
                        cell(item.name.toString(), nameWrap)
                        cell(item.wall1.top.toInt())
                        cell(item.wall1.bonus.toInt())
                        cell(item.wall1.at.toInt())
                        cell(item.wall1.ab.toInt())
                        cell(item.wall2.top.toInt())
                        cell(item.wall2.bonus.toInt())
                        cell(item.wall2.at.toInt())
                        cell(item.wall2.ab.toInt())
                        cell(item.wall3.top.toInt())
                        cell(item.wall3.bonus.toInt())
                        cell(item.wall3.at.toInt())
                        cell(item.wall3.ab.toInt())
                        cell(item.wall4.top.toInt())
                        cell(item.wall4.bonus.toInt())
                        cell(item.wall4.at.toInt())
                        cell(item.wall4.ab.toInt())
                        item.result?.top?.toInt()?.let { cell(it) }
                        item.result?.bonus?.toInt()?.let { cell(it) }
                        item.result?.at?.toInt()?.let { cell(it) }
                        item.result?.ab?.toInt()?.let { cell(it) }
                    }
                }
            }
        }
    }

    private fun Sheet.docHeader(){
        val headings = listOf("TOP", "ZONE", "ATTEMPT TO TOP", "ATTEMPT TO ZONE", "TOP", "ZONE", "ATTEMPT TO TOP", "ATTEMPT TO ZONE","TOP", "ZONE", "ATTEMPT TO TOP", "ATTEMPT TO ZONE","TOP", "ZONE", "ATTEMPT TO TOP", "ATTEMPT TO ZONE","TOP", "ZONE", "ATTEMPT TO TOP", "ATTEMPT TO ZONE",)

        val headingStyle = createCellStyle {
            setFont(createFont {
                fontName = "IMPACT"
                color = IndexedColors.WHITE.index
            })
            wrapText = true
            fillPattern = FillPatternType.SOLID_FOREGROUND
            fillForegroundColor = IndexedColors.AQUA.index
        }

        val headStyle = createCellStyle{
            borderTop = BorderStyle.THICK
            borderRight = BorderStyle.THICK
            borderBottom = BorderStyle.THICK
            borderLeft = BorderStyle.THICK

            setTopBorderColor(IndexedColors.BLACK.index)
            setRightBorderColor(IndexedColors.BLACK.index)
            setBottomBorderColor(IndexedColors.BLACK.index)
            setLeftBorderColor(IndexedColors.BLACK.index)
            alignment = HorizontalAlignment.CENTER
        }

        xssfSheet.addMergedRegion(CellRangeAddress(0,1,0,0))
        xssfSheet.addMergedRegion(CellRangeAddress(0,1,1,1))
        xssfSheet.addMergedRegion(CellRangeAddress(0,0,2,5))
        xssfSheet.addMergedRegion(CellRangeAddress(0,0,6,9))
        xssfSheet.addMergedRegion(CellRangeAddress(0,0,10,13))
        xssfSheet.addMergedRegion(CellRangeAddress(0,0,14,17))
        xssfSheet.addMergedRegion(CellRangeAddress(0,0,18,21))

        row(headStyle) {
            cell("No", createCellStyle{wrapText = true})
            cell("Name")
            cell("Dinding 1")
            cell("")
            cell("")
            cell("")
            cell("Dinding 2")
            cell("")
            cell("")
            cell("")
            cell("Dinding 3")
            cell("")
            cell("")
            cell("")
            cell("Dinding 4")
            cell("")
            cell("")
            cell("")
            cell("Total")

        }
        row(headingStyle) {
            cell("")
            cell("")
            headings.forEach { cell(it) }
        }
    }

    fun print(name : String? = "File.xlsx"){

        val dataName = if (name != "File.xlsx" ){
            name + ".xlsx"
        } else name

        var path = android.os.Environment.getExternalStorageDirectory()
        var tes = path.path + "/" + dataName
        dataToPrint.write(tes)
    }

}