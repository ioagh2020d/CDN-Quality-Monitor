import React from 'react';
import { Page, Text, View, Document, StyleSheet } from '@react-pdf/renderer';



// Create styles

const styles = StyleSheet.create({
    page: {
      flexDirection: 'column',
      fontFamily: 'Helvetica',
      // margin: 20,
      padding: 20
      // backgroundColor: '#E4E4E4'
    },
    paramSection: {
      margin: 10,
      padding: 10,

    },
    cdnSection: {
      
    },
    sectionHeader: {
      fontSize: 22,
      textAlign: 'center'

    },
    cdnHeader: {
      fontSize: 16,
      margin: 8
    },
    statsValue: {
      fontSize: 12,
      textIndent: 40
    },
    dateTime:{
      fontSize: 14,
      margin: 10,
      padding: 10,    

    }
  });
  
function mergeDeviations(data){
  const resultArr = []

  for(let i = 0; i < data.length; i+=2){
    const data1 = data[i].data.filter(d => d.y != null);
    const data2 = data[i+1].data.filter(d => d.y != null);
    resultArr.push({id: data[i+1].id, data: data1.concat(data2)})
    
  }
  return resultArr;
}


function calcMedian(values){
  if(values.length ===0) return 0;

  values.sort(function(a,b){
    return a-b;
  });

  const half = Math.floor(values.length / 2);

  if (values.length % 2)
    return values[half];

  return (values[half - 1] + values[half]) / 2.0;
}

function generateStatsSection(data, formData, unit){
  data = mergeDeviations(data);
  data = data.filter(d => {
    return formData[d.id.replaceAll('.','_')] === true
  })

  const elements = []

  for(const cdn of data){
    const values = cdn.data.map(a => a.y);
    const maxV = Math.max(...values);
    const minV = Math.min(...values);
    const avgV = (values.reduce((a, b) => a + b, 0) / values.length)
    const medianV = calcMedian(values);
    const dataCorrect = values.length > 0;
    elements.push(<View style={styles.cdnSection} key={cdn.id}>
        <Text style={styles.cdnHeader}>{cdn.id}</Text>
        {dataCorrect && <Text style={styles.statsValue}> max: {maxV} {unit}</Text>}
        {dataCorrect && <Text style={styles.statsValue}> min: {minV} {unit}</Text>}
        {dataCorrect && <Text style={styles.statsValue}> avg: {avgV} {unit}</Text>}
        {dataCorrect && <Text style={styles.statsValue}> median: {medianV} {unit}</Text>}
        {!dataCorrect && <Text style={styles.statsValue}> no data</Text>}
    </View>)
  }

  return elements;
}
// Create Document Component
const PDFReport = ({data, formData}) => {

  let RTTelements = [];
  let TpuElements = [];
  let PacketLossElements = [];
  
  if(data.rtt) RTTelements = generateStatsSection(data.rtt.data, formData, 'ms');
  if(data.throughput) TpuElements = generateStatsSection(data.throughput.data, formData, 'kb/s');
  if(data.packetLoss) PacketLossElements = generateStatsSection(data.packetLoss.data, formData, '%');


  return (<Document>
      <Page size="A4" style={styles.page}>
        <View style={styles.dateTime}>
          <Text >Start: {formData.startDate.toISOString()}</Text>
          <Text >End: {formData.endDate.toISOString()}</Text>
          <Text >Granularity: {formData.granularity}</Text>
        </View>
        {RTTelements.length > 0 &&<View style={styles.paramSection}>
          <Text style={styles.sectionHeader}>RTT</Text>
          {RTTelements}
        </View>}
        {TpuElements.length > 0 &&<View style={styles.paramSection}>
          <Text style={styles.sectionHeader}>Throughput</Text>
          {TpuElements}
        </View>}
        {PacketLossElements.length > 0 &&<View style={styles.paramSection}>
          <Text style={styles.sectionHeader}>PacketLoss</Text>
          {PacketLossElements}
        </View>}        
      </Page>
    </Document>);
}

  const generatePDFComponent = (data, formData) => {


    return <PDFReport data={data} formData={formData}/>
}

export default generatePDFComponent;