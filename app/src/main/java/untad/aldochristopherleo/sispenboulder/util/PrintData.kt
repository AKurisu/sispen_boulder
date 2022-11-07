package untad.aldochristopherleo.sispenboulder.util

import android.util.Log
import io.github.evanrupert.excelkt.Sheet
import io.github.evanrupert.excelkt.Workbook
import io.github.evanrupert.excelkt.workbook
import org.apache.poi.hssf.usermodel.HSSFCreationHelper
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.RegionUtil
import untad.aldochristopherleo.sispenboulder.data.SortedResult

class PrintData(val result: ArrayList<SortedResult>) {
    private lateinit var dataToPrint : Workbook

    init {
        init()
    }

    private fun init(){


        dataToPrint =
        workbook {
            sheet("File 1") {
                docHeader()
                val nameWrap = createCellStyle{
                    wrapText = true
                }
                var i = 1
                val rowLeftStyle = createCellStyle {
                    borderLeft = BorderStyle.THICK
                }
                val rowRightStyle = createCellStyle {
                    borderRight = BorderStyle.THICK
                }
                for(item in result){
                    row {
                        cell("")
                        cell(i++, nameWrap)
                        cell(item.name.toString(), nameWrap)
                        cell(item.wall1.top.toInt(), rowLeftStyle)
                        cell(item.wall1.bonus.toInt())
                        cell(item.wall1.at.toInt())
                        cell(item.wall1.ab.toInt())
                        cell(item.wall2.top.toInt(), rowLeftStyle)
                        cell(item.wall2.bonus.toInt())
                        cell(item.wall2.at.toInt())
                        cell(item.wall2.ab.toInt())
                        cell(item.wall3.top.toInt(), rowLeftStyle)
                        cell(item.wall3.bonus.toInt())
                        cell(item.wall3.at.toInt())
                        cell(item.wall3.ab.toInt())
                        cell(item.wall4.top.toInt(), rowLeftStyle)
                        cell(item.wall4.bonus.toInt())
                        cell(item.wall4.at.toInt())
                        cell(item.wall4.ab.toInt())
                        item.result?.top?.toInt()?.let { cell(it, rowLeftStyle) }
                        item.result?.bonus?.toInt()?.let { cell(it) }
                        item.result?.at?.toInt()?.let { cell(it) }
                        item.result?.ab?.toInt()?.let { cell(it, rowRightStyle) }
                    }
                }
            }
        }
    }

    private fun Sheet.docHeader(){
        val headings = listOf("TOP", "ZONE", "ATTEMPT TO TOP", "ATTEMPT TO ZONE", "TOP", "ZONE", "ATTEMPT TO TOP", "ATTEMPT TO ZONE","TOP", "ZONE", "ATTEMPT TO TOP", "ATTEMPT TO ZONE","TOP", "ZONE", "ATTEMPT TO TOP", "ATTEMPT TO ZONE","TOP", "ZONE", "ATTEMPT TO TOP", "ATTEMPT TO ZONE")

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

            topBorderColor = IndexedColors.BLACK.index
            rightBorderColor = IndexedColors.BLACK.index
            bottomBorderColor = IndexedColors.BLACK.index
            leftBorderColor = IndexedColors.BLACK.index
            alignment = HorizontalAlignment.CENTER
        }

        xssfSheet.addMergedRegion(range(0,1,1,1))
        xssfSheet.addMergedRegion(range(0,1,2,2))
        xssfSheet.addMergedRegion(range(0,0,3,6))
        xssfSheet.addMergedRegion(range(0,0,7,10))
        xssfSheet.addMergedRegion(range(0,0,11,14))
        xssfSheet.addMergedRegion(range(0,0,15,18))
        xssfSheet.addMergedRegion(range(0,0,19,22))

        row(headStyle) {
            cell("")
            cell("No")
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
            cell("")
            headings.forEach { cell(it) }
        }
    }

    fun print(name : String? = "File.xlsx"){

        val dataName = if (name != "File.xlsx" ){
            name + ".xlsx"
        } else name

        val path = android.os.Environment.getExternalStorageDirectory()
        val tes = path.path + "/" + dataName
        dataToPrint.write(tes)
    }

    private fun range(a: Int, b: Int, c: Int, d: Int): CellRangeAddress = CellRangeAddress(a,b,c,d)

}