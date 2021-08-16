package ru.glushko.testempty

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.net.URL
import org.xml.sax.InputSource
import javax.xml.parsers.DocumentBuilderFactory

open class ContentDownloadManager
{
    private val listOfValue:MutableList<String> = arrayListOf()

    open fun parseXMLFromWebsiteWithDateRange(date1:String, date2:String): MutableList<String> {
        lateinit var nNode: Node
        lateinit var result: NodeList
        val mainURL =
            URL("https://cbr.ru/scripts/XML_dynamic.asp?date_req1=$date1&date_req2=$date2&VAL_NM_RQ=R01235")
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val document = documentBuilder.parse(InputSource(mainURL.openStream()))
        document.documentElement.normalize()
        if(document.getElementsByTagName("Record") != null)
            result = document.getElementsByTagName("Record")

        for(item:Int in 0..result.length) {
            if(result.item(item) != null) {
                nNode = result.item(item)
            }

            val eElement:Element =  nNode as Element
            listOfValue.add("Day: ${item+1}\n Value: ${getNode(eElement)}")
        }
        return listOfValue
    } //Получение данных с сайта о динамики курса доллара.

    open fun parseXMLFromWebsiteWithSingleDate(date1:String, date2:String): String {
        var resulted = ""
        val nNode: Node
        lateinit var result: NodeList
        val mainURL =
            URL("https://cbr.ru/scripts/XML_dynamic.asp?date_req1=$date1&date_req2=$date2&VAL_NM_RQ=R01235")
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val document = documentBuilder.parse(InputSource(mainURL.openStream()))
        document.documentElement.normalize()
        if(document.getElementsByTagName("Record") != null)
            result = document.getElementsByTagName("Record")
        if(result.item(0) != null) {
            nNode = result.item(0)
            val eElement: Element = nNode as Element
            resulted = getNode(eElement)
        }

        return resulted
    } //Получение данных с сайта о текущем курсе доллара.

    private fun getNode(eElement:Element):String
    {
        val nList:NodeList = eElement.getElementsByTagName("Value").item(0).childNodes
        val nValue:Node = nList.item(0)
        return nValue.textContent
    } //Получение ноды из ХML данных.
}

