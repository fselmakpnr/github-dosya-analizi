/**
*
* @since 04.04.2024
* <p>
* Bu sınıf kullanıcıdan bir GitHub reposu URL'si ve bir hedef klasör yolu alır, bu depoyu klonlar,
*  içindeki Java dosyalarını bulur ve sınıf içeren dosyaların her birini inceler ardından istatistiklerini ayrı ayrı hesaplar.
* </p>
*/

package proje;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.println("Lutfen GitHub reposunun URL'sini girin: ");
            String repositoryUrl = reader.readLine();

            System.out.println("Lutfen kopyalanacak klasorun yolunu girin: ");
            String destinationPath = reader.readLine();

            File clonedDirectory = new File(destinationPath);
            if (clonedDirectory.exists() && clonedDirectory.isDirectory()) 
            {

                klasoruTemizle(clonedDirectory);
            }

            ProcessBuilder gitCloneProcessBuilder = new ProcessBuilder("git", "clone", repositoryUrl, destinationPath);
            Process gitCloneProcess = gitCloneProcessBuilder.start();
            gitCloneProcess.waitFor();

            System.out.println("GitHub deposu basariyla kopyalandi.");
            System.out.println("-----------------------------------");

            if (clonedDirectory.exists() && clonedDirectory.isDirectory()) 
            {
                javaDosyalariniBul(clonedDirectory);
            } 
            else 
            {
                System.out.println("Klonlanan dizin bulunamadi veya dizin değil.");
            }
        } 
        catch (IOException | InterruptedException e) 
        {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void klasoruTemizle(File klasor) 
    {
        File[] dosyalar = klasor.listFiles();
        if (dosyalar != null) 
        {
            for (File dosya : dosyalar) 
            {
                if (dosya.isDirectory()) 
                {
                    klasoruTemizle(dosya);
                } 
                else 
                {
                    dosya.delete();
                }
            }
        }
        klasor.delete();
    }

    public static void javaDosyalariniBul(File dizin) 
    {
        File[] dosyalar = dizin.listFiles();
        if (dosyalar != null) 
        {
            for (File dosya : dosyalar) 
            {
                if (dosya.isFile() && dosya.getName().endsWith(".java")) 
                {
                    javaDosyasiniIncele(dosya);
                } 
                else if (dosya.isDirectory()) 
                {
                    javaDosyalariniBul(dosya);
                }
            }
        } 
        else 
        {
            System.out.println("Klasor bos.");
        }
    }

    public static void javaDosyasiniIncele(File dosya) {
        try {
            
        	BufferedReader okuyucu = new BufferedReader(new FileReader(dosya));

            String satir;
            boolean sinifVarMi = false;

            
            while ((satir = okuyucu.readLine()) != null) 
            {
                satir = satir.trim();

                if (satir.contains("class")) 
                {
                    sinifVarMi = true;
                    break; 
                }
            }

           
            if (sinifVarMi) {
                
            	int javadocSatirSayisi = 0;
                int yorumSatirSayisi = 0;
                int kodSatirSayisi = 0;
                int fonksiyonSayisi = 0;
                int locSatirSayisi = 0;

                boolean javadocIcerisinde = false;
                boolean fonksiyonIcerisinde = false;

                
                okuyucu.close();
                okuyucu = new BufferedReader(new FileReader(dosya));

                while ((satir = okuyucu.readLine()) != null) 
                {
                    satir = satir.trim();
                    locSatirSayisi++;

                    if (satir.startsWith("/**")) 
                    {
                        javadocIcerisinde = true;
                    } 
                    else if (satir.endsWith("*/") && javadocIcerisinde) 
                    {
                        javadocIcerisinde = false;
                    } 
                    else if (javadocIcerisinde) 
                    {
                        javadocSatirSayisi++;
                    } 
                    else if (satir.startsWith("//") || satir.startsWith("//") || satir.endsWith("*/")) 
                    {
                        yorumSatirSayisi++;
                    } 
                    else if (!satir.isEmpty()) 
                    {
                        if (!satir.startsWith("/**") && !satir.startsWith("/*") && !satir.endsWith("*/") && !satir.startsWith("//") && !satir.startsWith("*")) 
                        {
                            kodSatirSayisi++;
                        }
                        if (satir.contains("//") && !satir.startsWith("//")) 
                        {
                            yorumSatirSayisi++;
                        }
                        if (satir.contains("(") && satir.contains(")") && (satir.contains("public") || satir.contains("private") || satir.contains("protected"))) 
                        {
                            fonksiyonSayisi++;
                            fonksiyonIcerisinde = true;
                        } 
                        else if (fonksiyonIcerisinde && satir.contains("}")) 
                        {
                            fonksiyonIcerisinde = false;
                        }
                    }
                }

               
                double YG = ((javadocSatirSayisi + yorumSatirSayisi) * 0.8) / fonksiyonSayisi;
                
                double YH = ((double) kodSatirSayisi / (double) fonksiyonSayisi) * 0.3;

                double yorumSapmaYuzdesi = ((100 * YG) / YH) - 100;
                yorumSapmaYuzdesi = Math.round(yorumSapmaYuzdesi * 100.0) / 100.0;

                System.out.println("Sinif: " + dosya.getName());
                System.out.println("Javadoc satir sayisi: " + javadocSatirSayisi);
                System.out.println("Yorum satiri sayisi: " + yorumSatirSayisi);
                System.out.println("Kod satiri sayisi: " + kodSatirSayisi);
                System.out.println("LOC: " + locSatirSayisi);
                System.out.println("Fonksiyon sayisi: " + fonksiyonSayisi);
                System.out.println("Yorum Sapma Yuzdesi:% " + yorumSapmaYuzdesi);
                System.out.println("---------------------------------------------");

                okuyucu.close();
            } 
            else 
            {
                
               System.out.println("Bu dosya sinif içermiyor.");
            	
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
