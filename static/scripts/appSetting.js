var screenshotMaxCount;
var appSetting = {
    screenshotCounter:0,
    imgType: 'screenshot',
    init: function(imgKeys, btnEle, inputEle) {
        this.initScreenshots(imageKeys);
        if (btnEle && inputEle) {
            this.bindUploadEvent(btnEle, inputEle);
        }
        this.showPassword();
    },
    bindUploadEvent: function(btnEle, inputEle) {
        btnEle.on('click', function() {
                    inputEle.click();
                    });
    },
    uploadImage: function(obj) {
        var that = this;
        //判断是否有选择上传文件
        var imgPath = $("#screenshot-input").val();
        if (imgPath == "") {
            alert("重新上传");
            return;
        }

        var fileList = $("#screenshot-input")[0].files;
        var listLength = fileList.length;
        if (listLength + this.screenshotCounter > screenshotMaxCount) {
            alert('截图最多上传' + screenshotMaxCount + '张');
            return false;
        }

        var formdata = new FormData(document.getElementById("upload-img-form"));
        formdata.append("filetype", this.imgType);
        var $loadDom = $('<div class="upload-loading col-md-2 text-center col-xs-6"><img src="/static/images/loading.gif"></div>');
        $.ajax({
            type:"post",
            dataType: "json",
            data: formdata,
            cache : false, 
            contentType : false, 
            processData : false,
            url: "/file/upload",
            beforeSend: function( xhr ) {
                screenshotTableContainer.append($loadDom);
                $("#submitBtn").attr('disabled', true);
            },
            success: function(result, textStatus, jqXHR) {
                var code = result.code;
                var extra = result.extra;
                $loadDom.remove();
                if(code == 0) {
                    extra.forEach(function(params, i) {
                        if (that.screenshotCounter >= screenshotMaxCount) {
                            return false;
                        }
                        ++that.screenshotCounter
                        that.displayPic(params);
                    });
                    that.setScreenshotCount();
                } else {
                    alert(result.message);
                }
                $("#submitBtn").attr('disabled', false);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                $loadDom.remove();
                alert("服务器出错");
                $("#submitBtn").attr('disabled', false);
            }
        });
    },

    showPassword: function() {
        var type = Number($("#installType").val());
        if (type === 2) {
            $("#settingPassword").removeClass('hide');
        } else {
            $("#settingPassword").addClass('hide');
        }
    },

    displayPic: function(params) {
        var html = `<div class="col-md-2 col-sm-3 col-xs-6 delete" data-key="${params.key}" data-index="${this.screenshotCounter}">
                        <div style="background-image:url(${params.url});" class="screenshot-item">
                        <span class="delete-filekey filekey-position" onclick="appSetting.deletePic('${params.key}')" data-key="${params.key}">
                        <i class="fa fa-minus" aria-hidden="true"></i>
                        </span>
                        </div>
                        <input type="hidden" name="attachmentImagesKeys[]" value="${params.key}">
                    </div> `;
        screenshotTableContainer.append(html);
        if(this.screenshotCounter >= screenshotMaxCount) {
            $("#uploader-screenshot").addClass('hide');
        }
    },

    deletePic: function(fileKey) {
        var flag = confirm('确认删除');
        if (flag) {
            $("div[data-key='"+fileKey+"']").remove();
            this.screenshotCounter--;
            $("#uploader-screenshot").removeClass('hide');
            this.setScreenshotCount();
        }
    },

    initScreenshots: function(imageKeys) {
        if (!imageKeys) {
            return false;
        }

        var preUrl = '/image/view/' + this.imgType;
        var imgSize = '1000';
        var index = 0;
        imageKeys.forEach(function(data,i) {
                            html = `<div class="col-md-2 col-sm-3 col-xs-6 delete" data-key="${data}" data-index="${i}">
                                        <div style="background-image:url(${preUrl}/${data}/${imgSize});" class="screenshot-item">
                                        <span class="delete-filekey filekey-position" onclick="appSetting.deletePic('${data}')" data-key="${data}">
                                        <i class="fa fa-minus" aria-hidden="true"></i>
                                        </span>
                                        </div>
                                        <input type="hidden" name="attachmentImagesKeys[]" value="${data}">
                                    </div><img class="hide" src="${preUrl}/${data}/${imgSize}"/>`;
                                    index++;
                                    $(html).appendTo(screenshotTableContainer);
                                    if(index >= screenshotMaxCount) {
                                        $("#uploader-screenshot").addClass('hide');
                                    }
                            });
        this.screenshotCounter = index;
        this.setScreenshotCount();
    },

    setScreenshotCount: function() {
        $("#upload-numbers").text(this.screenshotCounter);
    },

    ajaxSaveAppInfo: function() {
        $.ajax({
            type : "POST",
            data : $('#appSettingForm').serialize(),
            url : "/console/app/ajaxSaveAppInfo",
            dataType: 'json',
            cache: false,
            success : function(result) {
                var code = result.code
                if (code == 0) {
                    alert('保存成功');
                    window.location.reload();
                } else {
                    alert(result.message);
                }
            }
        });
    }
}
