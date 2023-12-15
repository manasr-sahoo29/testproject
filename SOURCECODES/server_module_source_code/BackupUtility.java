package imedix;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
		
/**
 * <center><b>iMediX Business Logic RMI Server </b></center>
 * <p>
 * Developted at Telemedicine Lab, IIT Kharagpur.
 * <p>
 * This class used for Backup Patient Data.
 * @author Saikat Ray.<br>Telemedicine Lab, IIT Kharagpur
 * @author <a href="mailto:skt.saikat@gmail.com">skt.saikat@gmail.com</a>
 * @see projinfo
 */
 
public class BackupUtility{
	Connection conn=null;
		
	Statement stmt2 = null;
	Statement stmta = null;
	Statement stmtchild = null;
	
	ResultSet RSet2 = null;
	ResultSet RSetchild = null;
	ResultSet RSet = null;

	PreparedStatement pstmt = null;
	projinfo pinfo;
	dball mydb;
	String backupdir="";
	String ccode="";
	
	/**
     * Constructor used to create a object.
     * @param p server Configuration class object.
     * @param code Center Code.
     * @param bkpdir Bachip Diractory Name.
     */
     	
	public BackupUtility(projinfo p,String code,String bkpdir){
		pinfo=p;
		mydb= new dball(pinfo);
		ccode=code;
		backupdir=bkpdir;
	}
	
	
	/**
 	* Method to create the Backup.  
 	*
 	* @param  patid	patient id.
 	* @return    	'Done' for Successful.
 	* @see        	getAllForms
 	* @see        	filestructure
 	* @see        	getformdata
 	* @see        	getBinaryData
 	* @see        	getMovieData
 	*/
     	
	public String createBackup(String patid){
		
			String strchk="";
			
			try{
				
				Class.forName(pinfo.gbldbjdbccriver);
				conn = DriverManager.getConnection(pinfo.gbldburl, pinfo.gbldbusername, pinfo.gbldbpasswd);
								
			}catch(Exception e){
				System.out.println(e.toString());
			}
		
			String allfroms=getAllForms(patid,conn);
			String sendrecords=allfroms;

			//System.out.println("WWERR : "+allfroms);
			String strtyp[]=allfroms.split("@");
			String forms[]=strtyp[0].split("#");
			String images[]=strtyp[1].split("#");
			String refimages[]=strtyp[2].split("#");
			String documents[]=strtyp[3].split("#");
			String movies[]=strtyp[4].split("#");

			filestructure(patid);   //creating file structure

			getformdata(patid,forms,conn); //calling getformdata function
			
			strchk=images[0];
			strchk=strchk.substring(strchk.indexOf(":")+1);
			
		
			if(strchk.length() > 1)
			{
				getBinaryData("img",patid,images);
			}
			strchk="";
	
			strchk=documents[0];
			strchk=strchk.substring(strchk.indexOf(":")+1);
			if(strchk.length() > 1)
			{
				getBinaryData("doc",patid,documents);
			}
			strchk="";

			strchk=refimages[0];
			strchk=strchk.substring(strchk.indexOf(":")+1);
			if(strchk.length() > 1)
			{
				getBinaryData("mark",patid,refimages);
			}
			strchk="";

			strchk=movies[0];
			strchk=strchk.substring(strchk.indexOf(":")+1);
			
			if(strchk.length() > 1)
			{
				getMovieData(patid,movies,conn);
			}
				
	
		return "done";
	
	}
	
	/**
 	* Method to create the Backup Folder Structure.  
 	*
 	* @param  pid	patient id.
 	*/
 		
	 private void filestructure(String pid )
		 {	String pp="";
			pp = pinfo.tempdatadir +"//backup//"+backupdir+"//"+pid.toUpperCase()+"//forms";
			File frmf=new File(pp);
			boolean yy=frmf.mkdirs();

			pp=pinfo.tempdatadir +"//backup//"+backupdir+"//"+pid.toUpperCase()+"//images";
			File imgf=new File(pp);
			boolean yy1=imgf.mkdirs();
			
			pp=pinfo.tempdatadir +"//backup//"+backupdir+"//"+pid.toUpperCase()+"//refimages";
			File reff=new File(pp);
			boolean yy2=reff.mkdirs();
			
			pp=pinfo.tempdatadir +"//backup//"+backupdir+"//"+pid.toUpperCase()+"//docs";
			File docf=new File(pp);
			boolean yy3=docf.mkdirs();

			pp=pinfo.tempdatadir +"//backup//"+backupdir+"//"+pid.toUpperCase()+"//movies";
			File movf=new File(pp);
			boolean yy4=movf.mkdirs();
						
		 }
		 	
	
	
	/**
 	* Method to create the Backup.  
 	*
 	* @param  allpat	A patient id.
 	* @param  conn1	MYSQl Connection Object.
 	* @return    	A formatted string of available records of a patient.
 	*/
 	
	private String getAllForms(String allpat,Connection conn1)
	{	
			String sqlfrm="",frmdata="",sern="",refsern="",sch="",schfrm="";
			
			ResultSet RSeta = null;
				
			try
			{
			
			stmta = conn1.createStatement();
			
			sqlfrm=(new StringBuilder()).append("SELECT DISTINCT type,serno FROM listofforms WHERE pat_id ='").append(allpat).append("' and type in (select name FROM forms)").toString();
			RSeta=stmta.executeQuery(sqlfrm);
			frmdata="forms:";
			while(RSeta.next())
			{
				sern=RSeta.getString("serno");
				if(sern.length()<2)
				sern="0"+sern;
				sch="select child from parchl where parent='"+RSeta.getString("type")+"'";
				stmtchild = conn1.createStatement();
				RSetchild=stmtchild.executeQuery(sch);
				if(RSetchild.next())
				{
				 schfrm=RSetchild.getString("child");
				}
				if(schfrm.equals(""))
				{
				frmdata+=RSeta.getString("type")+"-"+sern+"#";
				}
				else
				{
				frmdata+=RSeta.getString("type")+"-"+sern+"#";
				String childfrms[]=schfrm.split(",");
					for(int ch=0;ch<childfrms.length;ch++)
					{
					frmdata+=childfrms[ch]+"-"+sern+"#";
					}
				}
				schfrm="";
				RSetchild.close();
				stmtchild.close();
			} //end of while loop
			RSeta.close();

			sqlfrm="select type,serno from patimages where pat_id='"+allpat+"'";
			RSeta=stmta.executeQuery(sqlfrm);
			frmdata+="@images:";
			while(RSeta.next())
			{
				sern=RSeta.getString("serno");
				if(sern.length()<2)
				sern="0"+sern;
				frmdata+=RSeta.getString("type")+"-"+sern+"#";
			}
			RSeta.close();

			sqlfrm="select type,serno,img_serno from refimages where pat_id='"+allpat+"'";
			RSeta=stmta.executeQuery(sqlfrm);
			frmdata+="@refimages:";
			while(RSeta.next())
			{	refsern=RSeta.getString("img_serno");
				if(refsern.length()<2)
				refsern="0"+refsern;
				sern=RSeta.getString("serno");
				if(sern.length()<2)
				sern="0"+sern;
				frmdata+=RSeta.getString("type")+"-"+refsern+"-"+sern+"#";
			}
			RSeta.close();

			sqlfrm="select type,serno from patdoc where pat_id ='"+allpat+"'";
			RSeta=stmta.executeQuery(sqlfrm);
			frmdata+="@documents:";
			while(RSeta.next())
			{	
				sern=RSeta.getString("serno");
				if(sern.length()<2)
				sern="0"+sern;
				frmdata+=RSeta.getString("type")+"-"+sern+"#";
			}
			RSeta.close();

			sqlfrm="select type,serno from patmovies where pat_id ='"+allpat+"'";
			RSeta=stmta.executeQuery(sqlfrm);
			frmdata+="@movies:";
			while(RSeta.next())
			{	
				sern=RSeta.getString("serno");
				if(sern.length()<2)
				sern="0"+sern;
				frmdata+=RSeta.getString("type")+"-"+sern+"#";
			}
			RSeta.close();

						
			stmta.close();
			
			
			}
			catch(Exception exall)
			{
				System.out.println("frmdata :-"+exall.toString());
			}
			System.out.println("frmdata :---"+frmdata);
			System.out.println("sqlfrm :---"+sqlfrm);
			return frmdata;
		}
		
	
	private  void getformdata(String pid,String val[],Connection conn1)
		{
			String fname="",sn="",qryfld="",qryval="",infil="",dat="",dat1="",date="";
			String sqlQuery="";
						
			try
			{
			stmt2 = conn1.createStatement();		
			for(int k=0;k<val.length;k++)
			{
				String strname[]=val[k].split("-");
				fname=strname[0];
				fname=fname.substring(fname.indexOf(":")+1);
				sn=strname[1];
				sqlQuery="select * from "+fname.toLowerCase()+ " where pat_id='"+pid+"' AND serno="+sn;		
				RSet2=stmt2.executeQuery(sqlQuery);
				ResultSetMetaData rsmd = RSet2.getMetaData();
				int columnCount = rsmd.getColumnCount();
     			if(RSet2.next()==false) {
					continue;
				}
				RSet2.previous();
				
				while(RSet2.next())
				{	int col=1;
					if(!fname.equalsIgnoreCase("med"))
					col=2;
        			for( ;col <= columnCount-1; col++) {
				
					String data=RSet2.getString(col);
					date=rsmd.getColumnLabel(col);
					System.out.println(date);
					
					if(date.equalsIgnoreCase("longs")){
						date="longs";
					}
					if(date.equalsIgnoreCase("intervals")){
						date="interval";
					}
					if(date.equalsIgnoreCase("characters")){
						date="character";
					}
					
					if(date.equalsIgnoreCase("entrydate") || date.equalsIgnoreCase("testdate") || date.equalsIgnoreCase("apptdate") || date.equalsIgnoreCase("dateofbirth") )
					{
						System.out.println(date);
						if(data!=null)
						{
						dat=RSet2.getString(col);
						dat=dat.substring(8,10)+dat.substring(5,7)+dat.substring(0,4)+dat.substring(11,13)+dat.substring(14,16)+dat.substring(17,19);
						infil += rsmd.getColumnLabel(col) + "=" + dat + "\n";
						if(date.equalsIgnoreCase("entrydate")) dat1=dat;
						continue;	
						}
					}
					
					if(data==null)
					{
					infil += rsmd.getColumnLabel(col) + "=" + "\n";
					//System.out.println(rsmd.getColumnLabel(col)+"=");
					
					}
					else
					{
					//System.out.println(rsmd.getColumnLabel(col)+"="+RSet.getString(col));
					infil += rsmd.getColumnLabel(col) + "=" + RSet2.getString(col) + "\n";
					
					}
		
				}
			}
									
			String dirname = pinfo.tempdatadir +"//backup//"+backupdir+"//"+pid.toUpperCase()+"//forms";
			try
			{
			File f = new File(dirname);
			if(!f.exists())
			{
			boolean yes = f.mkdirs();
			}
			
			if(sn.length()<=1) sn="0"+sn;
			String filnam = dirname+"//"+pid+dat1+fname.toLowerCase()+sn+".form";
			FileOutputStream fout = new FileOutputStream(filnam);
			int i,t=0;
			// write in file
			byte b[] = new byte[infil.length()];
			infil.getBytes(0,b.length,b,0);
			fout.write(b);
			fout.close();
			}
			catch(IOException e1)
			{
				System.out.println("error : "+e1.toString());
			}
		
			infil="";
			qryfld="";
			qryval="";
			
			} //end of for loop
			RSet2.close();
			stmt2.close();
			//conn1.close();
			
			
			} // end of try block
			catch(Exception e)
			{
				
			}
				

		} // end of fn
		
	
	///////////////////
	private  void getBinaryData(String type,String patid,String imgval[])
		{
			String infile="",dt="",fname="",sn="",ext="",imgdirname="",imgnamdes="",psn="",imgnam="",rcode="";
			try
			{
			for(int k=0;k<imgval.length;k++)
			{	
				System.out.print("\nimgval : "+imgval[k]);

				if(type.equalsIgnoreCase("img"))
				{
				String strname[]=imgval[k].split("-");
				fname=strname[0];
				fname=fname.substring(fname.indexOf(":")+1);
				sn=strname[1];
				pstmt = conn.prepareStatement("SELECT patpic ,imgdesc,lab_name,doc_name,entrydate,con_type,ext,formkey FROM patimages WHERE upper(pat_id) = ? AND serno = ? AND upper(type) = ? ");
				pstmt.setString(1,patid.toUpperCase());
				pstmt.setString(2,sn);
				pstmt.setString(3,fname.toUpperCase());
				}
				if(type.equalsIgnoreCase("doc"))
				{
				String strname[]=imgval[k].split("-");
				fname=strname[0];
				fname=fname.substring(fname.indexOf(":")+1);
				sn=strname[1];
				pstmt = conn.prepareStatement("SELECT patdoc ,docdesc,lab_name,doc_name,entrydate,con_type,ext FROM patdoc WHERE upper(pat_id) = ? AND serno = ? AND upper(type) = ? ");
				pstmt.setString(1,patid.toUpperCase());
				pstmt.setString(2,sn);
				pstmt.setString(3,fname.toUpperCase());
				}
				if(type.equalsIgnoreCase("mark"))
				{
				String strname[]=imgval[k].split("-");
				fname=strname[0];
				fname=fname.substring(fname.indexOf(":")+1);
				sn=strname[1];
				psn=strname[2];
				pstmt = conn.prepareStatement("SELECT patpic,imgdesc,lab_name,doc_name,entrydate,con_type,ext,ref_code FROM refimages WHERE upper(pat_id) = ? AND img_serno = ? AND upper(type) = ? AND serno = ?");
				pstmt.setString(1,patid.toUpperCase());
				pstmt.setString(2,psn);
				pstmt.setString(3,fname.toUpperCase());
				pstmt.setString(4,sn);
				}

				if(sn.length()<=1) sn="0"+sn;
				if(psn.length()<=1) psn="0"+psn;
				
		
			RSet = pstmt.executeQuery();
			//byte[] fileArray = null;
				byte [] _blob = null;
				//RSet=stmt.executeQuery(sqlQuery);
				if(RSet.next()) {		
					System.out.println("I'm here : "+RSet.getString(6));
					if(!RSet.getString(6).equalsIgnoreCase("LRGFILE")){
					Blob blob = RSet.getBlob(1);
					int length = (int)blob.length();
					_blob = blob.getBytes(1, length);
					}
				
				infile+=RSet.getString(2)+"#";
				infile+=RSet.getString(3)+"#";
				infile+=RSet.getString(4)+"#";
				      
				if(type.equalsIgnoreCase("img") && fname.startsWith("i")) {
					String fkey=RSet.getString(8);
					if(fkey.length()<=1) fkey="0"+fkey;
					infile+=fkey+"#\n";
					}
				else infile+="#\n";
				
				dt=RSet.getString(5);
				dt=dt.substring(8,10)+dt.substring(5,7)+dt.substring(0,4)+dt.substring(11,13)+dt.substring(14,16)+dt.substring(17,19);
				infile+="Test Date:"+dt+"\n";
								
				infile+="ContentType:"+RSet.getString(6)+"\n";
				ext=RSet.getString(7);
				
				if(type.equalsIgnoreCase("img"))
				{
				imgdirname = pinfo.tempdatadir +"//backup//"+backupdir+"//"+patid.toUpperCase()+"//images";
				}
				if(type.equalsIgnoreCase("doc"))
				{
				imgdirname = pinfo.tempdatadir +"//backup//"+backupdir+"//"+patid.toUpperCase()+"//docs";
				}
				if(type.equalsIgnoreCase("mark"))
				{
				rcode=RSet.getString(8);
				imgdirname = pinfo.tempdatadir +"//backup//"+backupdir+"//"+patid.toUpperCase()+"//refimages";
				}


				try
				{
				File fimg = new File(imgdirname);
				if(!fimg.exists())
				{
				boolean yes = fimg.mkdirs();
				}
								
				if(type.equalsIgnoreCase("img"))
				{
				imgnam = imgdirname+"//"+patid+dt+fname.toLowerCase()+sn+"."+ext;
				imgnamdes = imgdirname+"//"+patid+dt+fname.toLowerCase()+sn+"."+ext+".txt";
				}
				if(type.equalsIgnoreCase("doc"))
				{
				imgnam = imgdirname+"//"+patid+dt+fname.toLowerCase()+sn+"."+ext;
				imgnamdes = imgdirname+"//"+patid+dt+fname.toLowerCase()+sn+"."+ext+".txt";
				}
				if(type.equalsIgnoreCase("mark"))
				{
				imgnam = imgdirname+"//"+patid+dt+fname.toLowerCase()+psn+rcode.toLowerCase()+sn+"."+ext;
				imgnamdes = imgdirname+"//"+patid+dt+fname.toLowerCase()+psn+rcode.toLowerCase()+sn+"."+ext+".txt";
				}
				
				FileOutputStream imgout = new FileOutputStream(imgnamdes);
				int i,t=0;
				// write in file
				byte b[] = new byte[infile.length()];
				infile.getBytes(0,b.length,b,0);
				imgout.write(b);
				imgout.close();
				
				
				RandomAccessFile raf = new RandomAccessFile(imgnam,"rw");
				raf.write(_blob);
				raf.close();
				}
				catch(IOException e1)
				{
				System.out.println("error : "+e1.toString());
				}
				
				} // end of if
				RSet.close();
				pstmt.close();
				infile="";
			} // end of for loop
			
			}
			catch(Exception e)
			{
				System.out.println("Error in getbinary data : "+e.toString());
			}


		} //end of function
		
	
	
	////////////
	
	 
	 	private  void getMovieData(String patid,String movval[],Connection conn1)
		{
			String infile="",movname="",msn="",dt="",movdirname="",movnam="",movnamdes="",mext="",dir="",movpath="";
			String movsrc="",msql="";
			try
			{
			 stmt2 = conn1.createStatement();
			 for(int k=0;k<movval.length;k++)
			{
				String mname[]=movval[k].split("-");
				movname=mname[0];
				movname=movname.substring(movname.indexOf(":")+1);
				msn=mname[1];
				msql="select * from patmovies where upper(pat_id)='"+patid.toUpperCase()+"' and type='"+movname.toLowerCase()+"' and serno="+msn;
				System.out.println("SQl "+msql);
				RSet2=stmt2.executeQuery(msql);
				byte [] _blob=null;
				
				while(RSet2.next())
				{
					infile+=RSet2.getString("movdesc")+"#";
					infile+=RSet2.getString("lab_name")+"#";
					infile+=RSet2.getString("doc_name")+"#";
				if(movname.startsWith("i"))
					{
					
					infile+=RSet2.getString("formkey")+"#\n";
				}
				else
					infile+="#\n";


					dt=RSet2.getString("entrydate");
					dt=dt.substring(8,10)+dt.substring(5,7)+dt.substring(0,4)+dt.substring(11,13)+dt.substring(14,16)+dt.substring(17,19);
					infile+="test date:"+dt+"\n";
					infile+="contenttype:"+RSet2.getString("con_type")+"\n";
					mext=RSet2.getString("ext");
					if(!RSet2.getString("con_type").equalsIgnoreCase("LRGFILE")){ 
						Blob blob = RSet2.getBlob("patmov");
						int length = (int)blob.length();
						_blob = blob.getBytes(1, length);
					}
					
				}
				
				if(msn.length()<=1) msn="0"+msn;
							
				movdirname = pinfo.tempdatadir +"//backup//"+backupdir+"//"+patid.toUpperCase()+"//movies";
				System.out.println("MOVPATH: "+movdirname);
				try
				{
				File fmov = new File(movdirname);
				if(!fmov.exists())
				{
				boolean yes = fmov.mkdirs();
				}
				movnam = movdirname+"//"+patid+dt+movname.toLowerCase()+msn+"."+mext;
				movnamdes = movdirname+"//"+patid+dt+movname.toLowerCase()+msn+"."+mext+".txt";
				
				FileOutputStream movout = new FileOutputStream(movnamdes);
				int i,t=0;
				// write in file
				
				byte b[] = new byte[infile.length()];
				infile.getBytes(0,b.length,b,0);
				movout.write(b);
				movout.close();
				
				
				RandomAccessFile raf = new RandomAccessFile(movnam,"rw");
				raf.write(_blob);
				raf.close();
				
				} catch(IOException e1){
					System.out.println(" Pat Mov Werror : "+e1.toString());
				}
								
				RSet2.close();
				//stmt2.close();
				infile="";
		
			}//end of for loop
			stmt2.close();

			} catch(Exception e){
				System.out.println("Pat Mov Error in Sql"+e.toString());
			}
		}
		
	 public static String delAllRecords(String string, projinfo projinfo2) throws Exception {
        Connection connection = null;
        Statement statement = null;
        String string2 = "Error";
        String string3 = "";
        try {
            Class.forName(projinfo2.gbldbjdbccriver);
            connection = DriverManager.getConnection(projinfo2.gbldburl, projinfo2.gbldbusername, projinfo2.gbldbpasswd);
            statement = connection.createStatement();
            String string4 = "SELECT DISTINCT type FROM listofforms WHERE pat_id ='" + string + "'";
            ResultSet resultSet = statement.executeQuery(string4);
            while (resultSet.next()) {
                String string5 = resultSet.getString("type");
                string3 = "Delete from " + string5 + " WHERE pat_id ='" + string + "'";
                BackupUtility.ExecuteSql(string3, projinfo2);
            }
            string3 = "Delete from patientvisit WHERE pat_id ='" + string + "'";
            BackupUtility.ExecuteSql(string3, projinfo2);
            string3 = "Delete from listofforms WHERE pat_id ='" + string + "'";
            BackupUtility.ExecuteSql(string3, projinfo2);
            string3 = "Delete from lpatq WHERE pat_id ='" + string + "'";
            BackupUtility.ExecuteSql(string3, projinfo2);
            string3 = "Delete from tpatq WHERE pat_id ='" + string + "'";
            BackupUtility.ExecuteSql(string3, projinfo2);
            string2 = "Done";
        }
        catch (Exception var6_7) {
            System.out.println(var6_7.toString());
            string2 = "Error";
        }
        finally {
            statement.close();
            connection.close();
        }
        return string2;
    }
    

    private static String ExecuteSql(String string, projinfo projinfo2) throws Exception {
        String string2 = "Error";
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName(projinfo2.gbldbjdbccriver);
            connection = DriverManager.getConnection(projinfo2.gbldburl, projinfo2.gbldbusername, projinfo2.gbldbpasswd);
            statement = connection.createStatement();
            int n = statement.executeUpdate(string);
            string2 = n == 0 ? "Error" : "Done";
        }
        catch (Exception var5_6) {
            System.out.println("Error ExecuteSql >> ::  " + var5_6.toString() + "\n" + string);
            string2 = "Error";
        }
        finally {
            statement.close();
            connection.close();
        }
        return string2;
    }
}
